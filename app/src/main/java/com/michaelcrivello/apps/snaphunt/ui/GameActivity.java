package com.michaelcrivello.apps.snaphunt.ui;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.GamePlayersAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Photo;
import com.michaelcrivello.apps.snaphunt.data.model.Round;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.event.PhotoReadyForSubmit;
import com.michaelcrivello.apps.snaphunt.event.RoundPhotoUpload;
import com.michaelcrivello.apps.snaphunt.event.S3UploadUpload;
import com.michaelcrivello.apps.snaphunt.ui.fragments.ThemeSelection;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
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
    protected Round currentRound;
    protected User currentJudge;
    protected List<UserDigest> players;
    protected Theme currentTheme;

    protected GamePlayersAdapter gamePlayersAdapter;
    protected GameEventListener gameEventListener;

    // File uploading
    private File selectedPhotoFile = null;
    private String selectedPhotoFilePath;
    private static final int IMAGE_SELECTED_CODE = 69;
    private static final int REQUEST_IMAGE_CAPTURE = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        getGameData(getGameIdFromIntent());

        gamePlayersAdapter = new GamePlayersAdapter(this);
        gameEventListener = new GameEventListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Ln.d("onStart");
//        bus.register(gameEventListener);
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

    @Override
    protected void onStop() {
        super.onStop();
        Ln.d("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Ln.d("onDestroy");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Ln.d("onRestart");
    }

    private void loadGameData(Game game) {
        this.game = game;
        this.currentRound = game.getRounds().get(game.getCurrentRound());
        this.currentTheme = currentRound.getSelectedTheme();

        gameStateCheck();

        // set TextViews
        roundStatusText.setText(currentRound.isActive() ? "Started" : "Not Started");
        String themeStr;
        if (currentTheme != null) {
            themeStr = "Theme: " + currentTheme.getPhrase();
        } else {
            themeStr = "No Theme Selected";
        }

        themeText.setText(themeStr);
        roundNumberText.setText("Current Round: " + game.getCurrentRound());

        // Add Header
        View header = getLayoutInflater().inflate(R.layout.listview_header, playersListView, false);
        TextView headerText = (TextView) header.findViewById(R.id.listViewHeaderText);
        headerText.setText("Players");

        playersListView.addHeaderView(header, null, false);

        // Setup Adapter
        gamePlayersAdapter.loadGame(game);
        playersListView.setAdapter(gamePlayersAdapter);

        // Photo
        // Disable submit button when there is no photo selected to upload
        submitPhotoButton.setEnabled(selectedPhotoFile != null);

    }

    // Check the game_activity state and handle appropriately, such as prompting Theme selection.
    private void gameStateCheck() {
        // TODO: Check if user is Judge. Check if theme has been selected, if not then show overlay.
    }


    @Override
    public void themeSelected(Theme theme) {
        // Make a network request to update the game_activity on the server that this rounds theme
        // has been selected. This will send push events to the players in the game_activity.

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
        bus.post(new RoundPhotoUpload(selectedPhotoFile));
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
        bus.register(gameEventListener);

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
        File file = photoReadyForSubmit.getFile();
        Ln.d("onPhotoReadyForSubmit. can read: " + file.canRead() + " path: " + file.getAbsolutePath());

        photoUrl.setText("Selected Photo: " + selectedPhotoFile.getPath());
        submitPhotoButton.setEnabled(file.canRead());
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
                                AmazonClientException e = upload.waitForException();
                                Ln.e("Unable to upload file to Amazon S3: " + e.getMessage());
                            } catch (InterruptedException e) {
                                Ln.e(e.getMessage());
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
    }

}
