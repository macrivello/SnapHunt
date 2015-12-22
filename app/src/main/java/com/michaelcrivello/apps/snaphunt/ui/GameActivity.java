package com.michaelcrivello.apps.snaphunt.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.SelectableUserDigestAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.game.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Photo;
import com.michaelcrivello.apps.snaphunt.data.model.round.Round;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.data.model.user.User;
import com.michaelcrivello.apps.snaphunt.data.model.user.UserDigest;
import com.michaelcrivello.apps.snaphunt.event.AWSTokenExpired;
import com.michaelcrivello.apps.snaphunt.event.AutoRefresh;
import com.michaelcrivello.apps.snaphunt.event.PhotoReadyForSubmit;
import com.michaelcrivello.apps.snaphunt.event.S3PhotoUpload;
import com.michaelcrivello.apps.snaphunt.event.S3Upload;
import com.michaelcrivello.apps.snaphunt.exception.TooManyItemsSelectedException;
import com.michaelcrivello.apps.snaphunt.misc.Selectable;
import com.michaelcrivello.apps.snaphunt.ui.fragments.ThemeSelection;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.view.PhotoImageView;
import com.michaelcrivello.apps.snaphunt.view.UserDigestListItemView;
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
import java.util.HashMap;
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
    private static final long ROUND_REFRESH_INTERVAL = 5000;

    @InjectView(R.id.gameOpenCameraButton)
    Button takePhotoButton;
    @InjectView(R.id.gameSubmitPhotoButton)
    Button submitPhotoButton;
    @InjectView(R.id.gameJudgeSelectPhotoButton)
    Button submitWinningPhotoButton;
    @InjectView(R.id.selectPhotoButton)
    Button selectPhotoButton;
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
    PhotoImageView photoPreview;
    @InjectView(R.id.gameFullscreenImageView)
    PhotoImageView fullscreenImageView;
    @InjectView(R.id.photoUrl)
    TextView photoUrl;
    @InjectView(R.id.stateInfoText)
    TextView stateInfoText;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    protected Game game;
    protected int currentRoundNumber;
    protected Round currentRound;
    protected Theme currentTheme;

    // TODO: This is not that helpful in terms of performance, I only want to download photo once.
    //    Photo model only contains photo url.
    // <UserId, Photo>
    protected HashMap<String, Photo> photos;

    protected SelectableUserDigestAdapter gamePlayersAdapter;
    protected GameEventListener gameEventListener;

    // File uploading
    private File selectedPhotoFile = null;
    private String selectedPhotoFilePath;
    private Photo submittedPhoto;
    private Photo selectedWinningPhoto;
    private Photo winningPhoto;
    private UserDigest winner;
    private String judgeId;
    private static final int IMAGE_SELECTED_CODE = 69;
    private static final int REQUEST_IMAGE_CAPTURE = 70;

    private boolean longClickOnItem;

    // Round Polling. BAD
    private Handler roundPollingHandler;
    private Runnable roundPollingRunnable;

    AlertDialog themeSelectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ln.d("Loading game");
        setContentView(R.layout.game_activity);

        // Limit selection to 1
        gamePlayersAdapter = new SelectableUserDigestAdapter(this, 1);
        gameEventListener = new GameEventListener();
        photos = new HashMap<>();

        // TODO: Replace with a better solution
        // Round Polling
        roundPollingHandler = new Handler();
        roundPollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (game != null) {
                    getCurrentRound();
                    roundPollingHandler.postDelayed(this, ROUND_REFRESH_INTERVAL);
                } else {
                    // stop handler
                    roundPollingHandler.removeCallbacksAndMessages(null);
                    Ln.e("Game is null. Stopping round refresh");
                }
            }
        };

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

        initToolbar();
        initDebugDrawer();
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

    private void initToolbar() {
        // Toolbar
        toolbar.setTitle(game != null ? "Game : " + game.getGameName() : "Game : ");
        toolbar.inflateMenu(R.menu.game_menu);

        MenuItem autoRefreshIc = toolbar.getMenu().findItem(R.id.action_auto_refresh);
        autoRefreshIc.getIcon().setAlpha(autoRefreshIc.isChecked() ? 255 : 100);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_auto_refresh:
                        toggleAutoReresh(menuItem);
                        return true;
                }

                return false;
            }
        });
    }

    private void toggleAutoReresh(MenuItem menuItem) {
        menuItem.setChecked(!menuItem.isChecked());

        autoRefresh = menuItem.isChecked();
        bus.post(new AutoRefresh(autoRefresh));

        if (autoRefresh) {
            menuItem.getIcon().setAlpha(255);
            Toast.makeText(this, "AUTO REFRESH: ON", Toast.LENGTH_SHORT).show();
        } else {
            menuItem.getIcon().setAlpha(100);
            Toast.makeText(this, "AUTO REFRESH: OFF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Ln.d("onPause");
        bus.unregister(gameEventListener);
        roundPollingHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Ln.d("onResume");
        bus.register(gameEventListener);
        if (autoRefresh) {
            roundPollingHandler.post(roundPollingRunnable);
        }
    }

    private void loadGameData(Game game) {
        this.game = game;
        updateToolbar(game);

        // TODO: Rounds are 1-indexed, currentRound is 0-indexed
        this.currentRoundNumber = game.getCurrentRound();

//        snaphuntApi.getRound(game.getGameIdAsString(), game.getRounds().get(currentRoundNumber).toHexString(), new Callback<Round>() {
//            @Override
//            public void success(Round round, Response response) {
//
//                loadCurrentRound(round);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Ln.e(error, "Error loading round Data");
//            }
//        });

        roundNumberText.setText("Current Round: " + (game.getCurrentRound()));

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

    private void updateToolbar(Game game) {
        if (toolbar != null) {
            toolbar.setTitle("Game : " + game.getGameName());
        }
    }

    private void getCurrentRound() {
        if (game != null && game.getRounds() != null && game.getRounds().get(currentRoundNumber-1) != null) {

            snaphuntApi.getRound(game.getGameIdAsString(), game.getRounds().get(currentRoundNumber-1).toHexString(), new Callback<Round>() {
                @Override
                public void success(Round round, Response response) {
                    loadCurrentRound(round);
                }

                @Override
                public void failure(RetrofitError error) {
                    Ln.e(error, "Error loading round Data");
                }
            });
        } else {
            Ln.e("Unable to call getRound. Invalid roundId");
        }
    }

    private void loadCurrentRound(Round round) {
        Ln.d("loading current round");
        if (round != null) {
            this.currentRound = round;

            // TODO: Set Judge on UserListItemView
            if (judgeId == null) {
                judgeId = round.getJudge().toHexString();

                SelectableUserDigestAdapter adapter = (SelectableUserDigestAdapter) ((HeaderViewListAdapter) playersListView.getAdapter()).getWrappedAdapter();
            }


            loadThemeData(currentRound);

            gameStateCheck();
        } else {
            Ln.e("Round == null");
        }

    }

    private void loadThemeData(Round round) {
        Ln.d("loading theme data");
        if (currentTheme == null) {
            if (round.getSelectedTheme() != null) {
                snaphuntApi.getTheme(game.getGameIdAsString(), currentRound.getId().toHexString(), round.getSelectedTheme().toHexString(), new Callback<Theme>() {
                    @Override
                    public void success(Theme theme, Response response) {
                        Ln.d("get selectedtheme");
                        setRoundTheme(currentTheme = theme);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Ln.e(error, "Error getting round themes themes");
                    }
                });
            } else {
                setRoundTheme(null);
            }
        } else {
            setRoundTheme(currentTheme);
        }
    }

    private void setRoundTheme(Theme currentTheme) {
        if (currentTheme != null) {
            themeText.setText("Theme: " + currentTheme.getPhrase());
        } else {
            themeText.setText("Theme: ");
        }
    }

    private void loadPlayerList(Game game) {
        // Playing around with Java 8 Streams, Lambdas. ('::' - Method Reference)
        Stream<ObjectId> userIdsAsObjects = Stream.of(game.getPlayers());
        List<String> userIds = userIdsAsObjects
                .map(ObjectId::toHexString)
                .filter(id -> !id.equals(userManager.getUserId()))
                .collect(Collectors.toList());


        snaphuntApi.listUsers(userIds, new Callback<List<UserDigest>>() {
            @Override
            public void success(List<UserDigest> users, Response response) {
                Ln.d("got player list");
                gamePlayersAdapter.loadUsers(users);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Failed to load users into ListView");
            }
        });
        playersListView.setAdapter(gamePlayersAdapter);
    }

    // TODO: Limit functionality based on isJudge()
    private void setClickListenersListView(ListView playersListView) {
        playersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // load photo into photo preview
                selectedWinningPhoto = null;
                // Grab userId, getRoundPhotoFromUserId.
                UserDigestListItemView itemView = (UserDigestListItemView) view;
                String userId = itemView.getUser().getId().toHexString();

                SelectableUserDigestAdapter adapter = (SelectableUserDigestAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter();

                // Should I just look up position?
                Selectable item = adapter.getSelectable(userId);

                Photo photo = photos.get(userId);
                if (photo == null) {
                    Ln.d("Fetching image for userId: " + userId);

                    snaphuntApi.getPhotoFromUserId(game.getGameIdAsString(), currentRound.getRoundIdAsString(), userId, new Callback<Photo>() {
                        @Override
                        public void success(Photo photo, Response response) {
                            Ln.d("Got submitted photo from user: " + userId);
                            photoPreview.setPhoto(photo);
                            selectedWinningPhoto = photo;
                            photos.put(userId, photo);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Ln.e("Error loading photo from user: " + userId);
                        }
                    });
                } else {
                    selectedWinningPhoto = photo;
                    photoPreview.setPhoto(photo);
                }

                try {
                    adapter.setSelected(item, !item.isSelected());
//                    itemView.setActivated(item.isSelected());
                } catch (TooManyItemsSelectedException e) {
                    Ln.d("Too many items selected in list");
                }

                // Only enable is judge and its time to pick photo
                if (isJudge() && currentRound != null && currentRound.getState().equals(Round.RoundState.JUDGE_SELECTION.name())) {
                    submitWinningPhotoButton.setEnabled(adapter.getSelectedItems().size() == 1);
                }
            }
        });

        playersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickOnItem = true;

                // Show the fullscreen photoview
                UserDigestListItemView itemView = (UserDigestListItemView) view;
                String userId = itemView.getUser().getId().toHexString();

                Photo photo = photos.get(userId);
                if (photo == null) {
                    Ln.d("Fetching image for userId: " + userId);

                    snaphuntApi.getPhotoFromUserId(game.getGameIdAsString(), currentRound.getRoundIdAsString(), userId, new Callback<Photo>() {
                        @Override
                        public void success(Photo photo, Response response) {
                            Ln.d("Got submitted photo from user: " + userId);
                            fullscreenImageView.setPhoto(photo);
                            fullscreenImageView.setVisibility(View.VISIBLE);
                            photos.put(userId, photo);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Ln.e("Error loading photo from user: " + userId);
                        }
                    });
                } else {
                    fullscreenImageView.setPhoto(photo);
                    fullscreenImageView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        playersListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (longClickOnItem) {
                        fullscreenImageView.setVisibility(View.GONE);
                        longClickOnItem = false;
                    }
                }
                return false;
            }
        });
    }

    // TODO: This needs major refactoring.
    // Check the game_activity state and handle appropriately, such as prompting Theme selection.
    private void gameStateCheck() {
        Ln.d("game state check");

        roundStatusText.setText("State: " + currentRound.getState());
        loadJudgeLayout(isJudge());

        // TODO: Do not hard code. Leverage enums.
        switch(game.getState()) {
            case "NOT_STARTED":
                Ln.d("Game has not started.");
                break;
            case "STARTED":
                Ln.d("Game has started.");
                break;
            case "ENDED":
                Ln.d("Game has ended.");
                break;
        }

        switch(currentRound.getState()) {
            case "NOT_STARTED":
                stateInfoText.setText(R.string.roundNotStarted);
                stateInfoText.setVisibility(View.VISIBLE);
                selectPhotoButton.setEnabled(false);
                submitPhotoButton.setEnabled(false);
                takePhotoButton.setEnabled(false);
                submitWinningPhotoButton.setEnabled(false);
                break;
            case "NO_THEME":
                submitWinningPhotoButton.setEnabled(false);

                if (!isJudge()){
                    stateInfoText.setText(R.string.waitingForTheme);
                    stateInfoText.setVisibility(View.VISIBLE);
                    selectPhotoButton.setEnabled(false);
                    submitPhotoButton.setEnabled(false);
                    takePhotoButton.setEnabled(false);

                //judge
                } else {
                    stateInfoText.setText(R.string.judgePickTheme);
                    stateInfoText.setVisibility(View.VISIBLE);

                    if (currentTheme == null) {
                        // let judge pick theme
                        displayThemeSelection();
                    } else {
                        // submit selected theme
                        themeSelected(currentTheme);
                    }
                }

                break;
            case "PLAYING":
                submitWinningPhotoButton.setEnabled(false);

                if (!isJudge()) {
                    // Waiting for other players to submit photos
                    // TODO: isUserPhotoSubmitted() should make network request. Need to update Round model.
                    boolean isUserPhotoSubmitted = isUserPhotoSubmitted();
                    selectPhotoButton.setEnabled(!isUserPhotoSubmitted);
                    submitPhotoButton.setEnabled(!isUserPhotoSubmitted);
                    takePhotoButton.setEnabled(!isUserPhotoSubmitted);

                    if (isUserPhotoSubmitted) {
                        // Needing to wait to load into s3 system
                        if (!photoPreview.isLocalBitmapLoaded()) {
                            photoPreview.setPhoto(submittedPhoto);
                        }
                        stateInfoText.setVisibility(View.INVISIBLE);
                    } else {
                        stateInfoText.setText(R.string.needToSubmitPhoto);
                        stateInfoText.setVisibility(View.VISIBLE);
                    }
                } else {
                    submitWinningPhotoButton.setEnabled(false);
                    stateInfoText.setText(R.string.waitingForPhotos);
                    stateInfoText.setVisibility(View.VISIBLE);
                }
                break;
            case "JUDGE_SELECTION":
                // All photos should be submitted

                if (!isJudge()) {
                    if (!photoPreview.isLocalBitmapLoaded()) {
                        photoPreview.setPhoto(submittedPhoto);
                    }
                    stateInfoText.setText(R.string.waitingForJudge);
                    stateInfoText.setVisibility(View.VISIBLE);
                    selectPhotoButton.setEnabled(false);
                    submitPhotoButton.setEnabled(false);
                    takePhotoButton.setEnabled(false);
                } else {
                    stateInfoText.setText(R.string.selectAPhoto);
                    stateInfoText.setVisibility(View.VISIBLE);
//                    submitWinningPhotoButton.setEnabled(true);

                    // TODO: When do these get disabled?
                    setClickListenersListView(playersListView);
                }
                break;
            case "ENDED":
                // TODO: Load winning photo
                if (winningPhoto == null) {
                    snaphuntApi.getPhoto(currentRound.getWinningPhoto().toHexString(), new Callback<Photo>() {
                        @Override
                        public void success(Photo photo, Response response) {
                            Ln.d("got winning photo");
                            winningPhoto = photo;
                            updateWinningPhoto(winningPhoto);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Ln.e("Error downloading winning photo");
                        }
                    });
                } else {
                    if (winner == null) {
                        stateInfoText.setText(R.string.roundEnded);
                        stateInfoText.setVisibility(View.VISIBLE);

                        snaphuntApi.getUser(currentRound.getWinner().toHexString(), new Callback<UserDigest>() {
                            @Override
                            public void success(UserDigest user, Response response) {
                                winner = user;
                                updateWinner(winner);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                    } else {
                        updateWinner(winner);
                    }
                }

                selectPhotoButton.setEnabled(false);
                submitPhotoButton.setEnabled(false);
                takePhotoButton.setEnabled(false);
                submitWinningPhotoButton.setEnabled(false);
                break;

        }
    }

    private void updateWinner(UserDigest user) {
        stateInfoText.setText(user.getUsername() + " wins the round!");
        stateInfoText.setVisibility(View.VISIBLE);
        if (stateInfoText.getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stateInfoText.setVisibility(View.INVISIBLE);
                    setClickListenersListView(playersListView);
                }
            }, 5000);
        }
    }

    private void updateWinningPhoto(Photo photo) {
        photoPreview.setPhoto(photo);
    }

    // I'm gonna hit the network on this one to ensure photo is sittin in DB
    // TODO: this will need to be optimized and cached for performance
    private boolean isUserPhotoSubmitted() {
        return submittedPhoto != null;
    }

    private void loadJudgeLayout(Boolean b) {
        selectPhotoButton.setVisibility(b ? View.GONE : View.VISIBLE);
        submitPhotoButton.setVisibility(b ? View.GONE : View.VISIBLE);
        takePhotoButton.setVisibility(b ? View.GONE : View.VISIBLE);
        submitWinningPhotoButton.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    // TODO: need theme generation
    public boolean isThemeSelected() {
        return currentRound != null && currentRound.getSelectedTheme() != null;
    }

    // TODO: verify that judge is being set properly on backend
    private boolean isJudge() {
        String judgeId = currentRound != null ? currentRound.getJudge().toHexString() : "";
        return userManager.getUserId().equals(judgeId);
    }

    // TODO: Make a custom view for theme selector.
    private void displayThemeSelection() {
        if (themeSelectionDialog == null || !themeSelectionDialog.isShowing())
        snaphuntApi.getThemes(game.getGameIdAsString(), currentRound.getRoundIdAsString(), new Callback<List<Theme>>() {
            @Override
            public void success(List<Theme> themes, Response response) {
                showThemeSelection(themes);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error loading list of themes");
            }
        });



    }

    private void showThemeSelection(List<Theme> themes) {
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

                // TODO: dismiss() ?
            }
        });
        themeSelectionDialog = builder.show();
    }

    @Override
    public void themeSelected(Theme theme) {
        // Make a network request to update the game_activity on the server that this rounds theme
        // has been selected. This will send push events to the players in the game_activity.
        snaphuntApi.selectTheme(game.getGameIdAsString(), currentRound.getId().toHexString(), theme.getId().toHexString(), new Callback<Round>() {
            @Override
            public void success(Round round, Response response) {
                Ln.d("Selected theme for Round");
                currentRound = round;
                currentTheme = theme;
                loadCurrentRound(currentRound);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e(error, "Error selecting theme for Round");
            }
        });
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

    // Submit winning photo
    public void onWinningPhotoSubmit(View v) {
        Ln.d("onWinningPhotoSubmit");
        if (selectedWinningPhoto != null){
            snaphuntApi.submitWinner(game.getGameIdAsString(), currentRound.getRoundIdAsString(), selectedWinningPhoto.getId().toHexString(), new Callback<Round>() {
                @Override
                public void success(Round round, Response response) {
                    Ln.d("Selected winning photo");
                    loadCurrentRound(round);
                }

                @Override
                public void failure(RetrofitError error) {
                    Ln.d("Error selecting winning photo");
                }
            });
        } else {
            Ln.e("selectedWinningPhoto is null");
        }
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
    protected void handleS3Upload(S3Upload s3Upload) {
        Ln.d("onS3Upload");
        // Add listener to Upload
        s3Upload.getUpload().addProgressListener(new S3UploadProgressListener(s3Upload));
    }

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
        S3Upload s3Upload;
        protected Upload upload;
        protected File file;
        protected String bucket, key;

        public S3UploadProgressListener(S3Upload s3Upload) {
            this.s3Upload = s3Upload;
            this.upload = s3Upload.getUpload();
            this.file = s3Upload.getUploadedFile();
            this.bucket = s3Upload.getBucket();
            this.key = s3Upload.getKey();
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

                            submitPhoto(s3Upload);
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
                            submitPhotoButton.setEnabled(true);

                            break;
                        case ProgressEvent.STARTED_EVENT_CODE:
                            Ln.d("Upload Started: " + upload.getDescription());
                            Toast.makeText(getBaseContext(), "Upload starting for: " + upload.getDescription(), Toast.LENGTH_LONG).show();
                            submitPhotoButton.setEnabled(false);
                            break;
                    }
                }
            });
        }

        private void submitPhoto(S3Upload s3Upload) {
            Ln.d("submitting photo to snaphunt db");
            /* AmazonS3EncryptionClient is another implementing class of AmazonS3, which .getAmazonS3Client
              returns. I check source code, and saw that transfermanager constructs AmazonS3Client.
              Casting for getUrl(), not in AmazonS3 interface*/
            String photoUrl = ((AmazonS3Client)transferManager.getAmazonS3Client()).getResourceUrl(s3Upload.getBucket(), s3Upload.getKey());
            if (photoUrl == null) {
                //TODO: handle this error.
                Ln.e("Invalid URL for photo. Aborting photo Submitted");
                return;
            }

            Photo photo = new Photo();
            photo.setUrl(photoUrl);
            // Server will handle setting other fields.

            snaphuntApi.submitPhoto(photo, game.getGameIdAsString(), currentRound.getRoundIdAsString(), new Callback<Photo>() {
                @Override
                public void success(Photo photo, Response response) {
                    Ln.d("Successfully submitted for for round");

                    submittedPhoto = photo;
                    // I can load s3 resource immediately, needs time to get stored on s3.

//                    photoPreview.setPhoto(photo);

                    progressWheel.setProgress(100);
                    progressWheel.setVisibility(View.GONE);
                    photoPreview.setAlpha(1F);

                    file.delete();
                }

                @Override
                public void failure(RetrofitError error) {
                    Ln.e("Error submitting photo: " + error.getMessage());
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
        public void onS3Upload(S3Upload s3Upload){
            Ln.d("onS3Upload");

            handleS3Upload(s3Upload);
        }

        @Produce
        public PhotoReadyForSubmit producePhotoReadForSubmit() {
            return new PhotoReadyForSubmit(selectedPhotoFile);
        }
    }

    @Override
    public void autoRefresh(boolean b) {
        Ln.d(b ? "Auto refresh ON" : "Auto refresh Off");
        roundPollingHandler.removeCallbacksAndMessages(null);

        if (b) {
            roundPollingHandler.post(roundPollingRunnable);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (submittedPhoto != null){
            outState.putSerializable(Constants.USER_SUBMITTED_PHOTO, submittedPhoto);
        }

        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        submittedPhoto = (Photo) savedInstanceState.getSerializable(Constants.USER_SUBMITTED_PHOTO);
    }

}
