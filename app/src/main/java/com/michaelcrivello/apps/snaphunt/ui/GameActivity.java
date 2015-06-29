package com.michaelcrivello.apps.snaphunt.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.UserDigestAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Round;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.event.AWSTokenExpired;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.event.PhotoReadyForSubmit;
import com.michaelcrivello.apps.snaphunt.event.S3PhotoUpload;
import com.michaelcrivello.apps.snaphunt.event.S3UploadUpload;
import com.michaelcrivello.apps.snaphunt.ui.fragments.ThemeSelection;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by michael on 3/24/15.
 */
public class GameActivity extends BaseActivity implements ThemeSelection {
    @InjectView(R.id.gameOpenCameraButton)
    Button takePhotoButton;
    @InjectView(R.id.gameSubmitPhotoButton)
    Button submitPhotoButton;
    @InjectView(R.id.gamePlayersListView)
    ListView playersListView;
    @InjectView(R.id.gameRoundStatusText)
    TextView roundStatusText;
    @InjectView(R.id.gameThemeText)
    TextView themeText;
    @InjectView(R.id.gameRoundNumberText)
    TextView roundNumberText;
    @InjectView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @InjectView(R.id.photoPreview)
    ImageView photoPreview;
    @InjectView(R.id.photoUrl)
    TextView photoUrl;

    protected Game game;
    protected int currentRoundNumber;
    protected Round currentRound;
    protected User currentJudge;
    protected List<UserDigest> players;
    protected Theme currentTheme;

    protected UserDigestAdapter gamePlayersAdapter;
    protected GameEventListener gameEventListener;


    // File uploading
    private File selectedPhotoFile = null;
    private String selectedPhotoFilePath;
    private static final int IMAGE_SELECTED_CODE = 69;
    private static final int REQUEST_IMAGE_CAPTURE = 70;
    private boolean themeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ln.d("Loading game");
        setContentView(R.layout.game_activity);

        gamePlayersAdapter = new UserDigestAdapter(this);
        gameEventListener = new GameEventListener();

        Intent intent = getIntent();
        Game intentGame;
        if ((intentGame = (Game) intent.getSerializableExtra(Constants.GAME_KEY)) != null) {
            if (intent.hasExtra(Constants.ACCEPTING_INVITE)) {
                acceptInvitation(intentGame.getGameIdAsString());
            } else {
                loadGameData(intentGame);
            }
        } else {
            if (intent.hasExtra(Constants.ACCEPTING_INVITE)) {
                acceptInvitation(getGameIdFromIntent());
            } else {
                getGameData(getGameIdFromIntent());
            }
        }
    }

    private void acceptInvitation(String gameId) {
        snaphuntApi.acceptInvite(gameId, new Callback<Game>() {
            @Override
            public void success(Game game, Response response) {
                Ln.d("Accepted game invite, updating game");
                loadGameData(game);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error accepting invite", error);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Ln.d("onPause");
        bus.unregister(gameEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Ln.d("onResume");
        bus.register(gameEventListener);
    }

    private void loadGameData(Game game) {
        this.game = game;

        // TODO: Rounds are 1-indexed, currentRound is 0-indexed
        this.currentRoundNumber = game.getCurrentRound();

        snaphuntApi.getRound(game.getGameIdAsString(), game.getRounds().get(currentRoundNumber).toHexString(), new Callback<Round>() {
            @Override
            public void success(Round round, Response response) {
                loadCurrentRound(round);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e(error, "Error loading round Data");
            }
        });
        // TODO: hit getRound endpoint to get round data.


        roundNumberText.setText("Current Round: " + game.getCurrentRound() + 1);

        // Add Header
        View header = getLayoutInflater().inflate(R.layout.listview_header, playersListView, false);
        TextView headerText = (TextView) header.findViewById(R.id.listViewHeaderText);
        headerText.setText("Players");

        playersListView.addHeaderView(header, null, false);

        // Setup Adapter
        loadPlayerList(game);

        // Photo
        // Disable submit button when there is no photo selected to upload
        submitPhotoButton.setEnabled(selectedPhotoFile != null);

    }

    private void loadCurrentRound(Round round) {
        Ln.d("loading current round");
        if (round != null) {
            this.currentRound = round;
            loadThemeData(currentRound);

            // set TextViews
            roundStatusText.setText(currentRound.isActive() ? "Started" : "Not Started");

        } else {
            Ln.e("Round == null");
        }

        gameStateCheck();

    }

    private void loadThemeData(Round round) {
        Ln.d("loading theme data");
        if (round.getSelectedTheme() != null) {
            snaphuntApi.getTheme(game.getGameIdAsString(), currentRound.getId().toHexString(), round.getSelectedTheme().toHexString(), new Callback<Theme>() {
                @Override
                public void success(Theme theme, Response response) {
                    Ln.d("get selectedtheme");
                    currentTheme = theme;
                    themeText.setText("Theme: " + currentTheme.getPhrase());
                }

                @Override
                public void failure(RetrofitError error) {
                    Ln.e(error, "Error getting round themes themes");
                }
            });
        } else {
            themeText.setText("No Theme Selected");
        }
    }

    private void loadPlayerList(Game game) {
        // Playing around with Java 8 Streams, Lambdas. ('::' - Method Reference)
        Stream<ObjectId> userIdsAsObjects = Stream.of(game.getPlayers());
        List<String> userIds = userIdsAsObjects
                .map(ObjectId::toHexString)
                .filter(id -> !id.equals(userManager.getUserDigestId()))
                .collect(Collectors.toList());

        snaphuntApi.getUserDigestList(userIds, new Callback<List<UserDigest>>() {
            @Override
            public void success(List<UserDigest> userDigests, Response response) {
                Ln.d("got player list");
                gamePlayersAdapter.loadUsers(userDigests);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Failed to load users into ListView");
            }
        });
        playersListView.setAdapter(gamePlayersAdapter);
    }

    // Check the game_activity state and handle appropriately, such as prompting Theme selection.
    private void gameStateCheck() {
        // TODO: Check if user is Judge. Check if theme has been selected, if not then show overlay.
        Ln.d("game state check");
        if (isJudge()) {
            photoPreview.setImageDrawable(getDrawable(R.drawable.judge_display));

            if (!isThemeSelected()) {
                snaphuntApi.getThemes(game.getGameIdAsString(), currentRound.getId().toHexString(), new Callback<List<Theme>>() {
                    @Override
                    public void success(List<Theme> themes, Response response) {
                        displayThemeSelection(themes);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Ln.e(error, "Error getting round themes");
                    }
                });
             }
        }
    }

    // TODO: need theme generation
    public boolean isThemeSelected() {
        return currentRound != null && currentRound.getSelectedTheme() != null;
    }

    // TODO: verify that judge is being set properly on backend
    private boolean isJudge() {
        String judgeId = currentRound != null ? currentRound.getJudge().toHexString() : "";
        return userManager.getUserDigestId().equals(judgeId);
    }

    // TODO: Make a custom view for theme selector.
    private void displayThemeSelection(List<Theme> themes) {
        List<String> phrases = new ArrayList<>();
        Stream.of(themes).forEach(t -> phrases.add(t.getPhrase()));

        // TODO: Make theme adapter, pass in list of Themes
        // temp using basic alertdialog
        CharSequence[] themeArray = phrases.toArray(new CharSequence[themes.size()]);

        // TODO: list adapter
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a theme: ");
        builder.setItems(themeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Theme selectedTheme = themes.get(which);
                Ln.d("Picked theme: " + selectedTheme.getPhrase());

                themeSelected(selectedTheme);
            }
        });
        builder.show();
    }

    @Override
    public void themeSelected(Theme theme) {
        // Make a network request to update the game_activity on the server that this rounds theme
        // has been selected. This will send push events to the players in the game_activity.
        snaphuntApi.selectTheme(game.getGameIdAsString(), currentRound.getId().toHexString(), theme.getId().toHexString(), new Callback<Round>() {
            @Override
            public void success(Round round, Response response) {
                Ln.d("Selelected theme for Round");
                currentRound = round;
                loadCurrentRound(currentRound);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e(error, "Error selecting theme for Round");
            }
        });

        // The Round object itself needs to be updated too. Round.selectedTheme.

        // Remove the overlay fragment

        // Update the Game status square with the theme.
    }

    public void getGameData(final String gameId) {
        snaphuntApi.getGame(gameId, new Callback<Game>() {
            @Override
            public void success(Game game, Response response) {
                Ln.d("Loading Game data for gameId: " + game.getGameIdAsString());
                loadGameData(game);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error downloading Game object for gameId: " + gameId);

            }
        });
    }

    //TODO refactor, too verbose
    public String getGameIdFromIntent() {
        String gameId = null;
        Bundle b;
        if ((b = getIntent().getExtras()) != null) {
            gameId = b.getString(Constants.GAME_ID_KEY, "");

            if (!gameId.isEmpty()) {
                Ln.d("Recieved gameId from intent: " + gameId);
            } else {
                // No game_activity data passed to Activity. Route back to HomePage for now.
                // TODO: Handle an empty gameId
                Ln.e("Empty gameId recieved from intent.");
            }
        }

        return gameId;
    }

    // Open Gallery to select photo
    public void selectPhoto(View v) {
        launchGalleryImageSelector();
    }

    // Open up camera to take photo
    public void onTakePhotoClick(View v) {
        launchCamera();
    }

    // Submit photo
    public void onSubmitPhotoClick(View v) {
        Ln.d("Attempting to upload file.");

        // Upload progress is returned as S3TransferProgress event
        bus.post(new S3PhotoUpload(selectedPhotoFile));
    }

    // Start ActivityfoForResult intent with MediaStore.ACTION_IMAGE_CAPTURE. Opens Camera.
    // Stores image in file "selectedPhotoFile"
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            selectedPhotoFile = null;
            try {
                selectedPhotoFile = createImageFile();
            } catch (IOException ex) {
                selectedPhotoFile = null;
                Ln.e(ex.getMessage());
            }

            if (selectedPhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(selectedPhotoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedPhotoFilePath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void writeBitmapToFile(Bitmap bitmap, File selectedPhotoFile) throws Exception {
        OutputStream os;
        try {
            os = new FileOutputStream(selectedPhotoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            Ln.d("posting PhotoReadyForSubmit event");
            bus.post(new PhotoReadyForSubmit(selectedPhotoFile));
        } catch (Exception e) {
            Ln.e("Error writing bitmap to file");
        }

    }

    private void launchGalleryImageSelector() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Photo"), IMAGE_SELECTED_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Ln.d("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_SELECTED_CODE:
                if (resultCode != RESULT_OK || data.getData() == null) {
                    Ln.d("No image selected");
                    return;
                }

                handleImageSelected(data);
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode != RESULT_OK) {
                    Ln.d("No photo taken");
                    return;
                }

                handleCameraResult(data);
                break;
        }
    }

    private void handleCameraResult(Intent data) {
        if (selectedPhotoFile != null) {

            Bitmap bitmap = BitmapFactory.decodeFile(selectedPhotoFile.getAbsolutePath());
            photoPreview.setAlpha(0.7F);
            photoPreview.setImageBitmap(bitmap);

            Ln.d("posting PhotoReadyForSubmit event");
            bus.post(new PhotoReadyForSubmit(selectedPhotoFile));
        }

    }

    private void handleImageSelected(Intent data) {
        selectedPhotoFile = null;

        Uri imageUri = data.getData();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            photoPreview.setAlpha(0.7F);
            photoPreview.setImageBitmap(bitmap);

            selectedPhotoFile = null;
            try {
                selectedPhotoFile = createImageFile();
                writeBitmapToFile(bitmap, selectedPhotoFile);
            } catch (Exception e) {
                selectedPhotoFile = null;
                e.printStackTrace();
                Ln.e(e.getMessage());
            }
        }
    }


    // Overriden Methods triggered by Subscribed events on BaseActivity
    @Override
    protected void handleS3Upload(S3UploadUpload s3UploadUpload) {
        Ln.d("onS3Upload");
        Upload upload = s3UploadUpload.getUpload();
        File file = s3UploadUpload.getUploadedFile();

        upload.addProgressListener(new S3UploadProgressListener(upload, file));
    }

    @Override
    protected void handlePhotoReady(PhotoReadyForSubmit photoReadyForSubmit) {
        Ln.d("onPhotoReadyForSubmit");
        File file = photoReadyForSubmit.getFile();
        if (file != null && file.canRead()) {
            photoUrl.setText("Selected Photo: " + selectedPhotoFile.getPath());
            submitPhotoButton.setEnabled(file.canRead());
        }
    }

    // Inner Class
    private class S3UploadProgressListener implements ProgressListener {
        protected Upload upload;
        protected File file;

        public S3UploadProgressListener(Upload upload, File file) {
            this.upload = upload;
            this.file = file;
        }

        @Override
        public void progressChanged(final ProgressEvent progressEvent) {
            if (upload == null) return;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int percentTransfered = ((int) upload.getProgress().getPercentTransferred());
                    if (percentTransfered > 0) {
                        if (progressWheel.getVisibility() == View.INVISIBLE) {
                            progressWheel.setVisibility(View.VISIBLE);
                        }
                        progressWheel.setProgress(percentTransfered);
                    }

                    switch (progressEvent.getEventCode()) {
                        case ProgressEvent.COMPLETED_EVENT_CODE:
                            Ln.d("Upload Complete: " + upload.getDescription());
                            progressWheel.setProgress(100);
                            progressWheel.setVisibility(View.GONE);
                            photoPreview.setAlpha(1F);
                            photoUrl.setText(upload.getDescription());
                            file.delete();
                            break;
                        case ProgressEvent.FAILED_EVENT_CODE:
                            try {
                                UploadResult uploadResult = upload.waitForUploadResult();
                            } catch (InterruptedException e) {
                                Ln.e(e.getMessage());
                            } catch (AmazonServiceException e) {
                                if (("ExpiredToken".equals(e.getErrorCode()))) {
                                    Ln.d("Expired AWS Token. Posting event to refresh credentials.");
                                    // Expired Token. Refresh S3Client
                                    // post event on bus with pending upload to retry. repost s3UploadEvent
                                    bus.post(new AWSTokenExpired(new S3PhotoUpload(file),null));
                                }
                            }

                            break;
                        case ProgressEvent.STARTED_EVENT_CODE:
                            Ln.d("Upload Started: " + upload.getDescription());
                            Toast.makeText(getBaseContext(), "Upload starting for: " + upload.getDescription(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        }
    }

    protected class GameEventListener {
        // Event Subscription
        @Subscribe
        public void onPhotoReady(PhotoReadyForSubmit photoReadyForSubmit){
            Ln.d("onPhotoReady");

            handlePhotoReady(photoReadyForSubmit);
        }
        @Subscribe
        public void onS3Upload(S3UploadUpload s3UploadUpload){
            Ln.d("onS3Upload");

            handleS3Upload(s3UploadUpload);
        }
        @Subscribe
        public void onGcmRegistered (GcmRegistered gcmRegistered) {
            Ln.d("onGcmRegistered");
            userManager.updateUserGcmId(gcmRegistered.getRegId());
        }
        @Subscribe
        public void onGcmUnregistered (GcmUnregistered gcmUnregistered) {
            Ln.d("onGcmUnregistered");
        }

        @Produce
        public PhotoReadyForSubmit producePhotoReadForSubmit() {
            return new PhotoReadyForSubmit(selectedPhotoFile);
        }
    }
}
