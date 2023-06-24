package Client.Controllers;

import Client.ClientManager;
import Client.Enums.PlayStatus;
import Client.LoadManager;
import Shared.Dto.File.FileDto;
import Shared.Dto.Music.DislikeMusicDto;
import Shared.Dto.Music.FindOneMusicDto;
import Shared.Dto.Music.LikeMusicDto;
import Shared.Dto.Playlist.AddMusicToPlaylistDto;
import Shared.Entities.CommentEntity;
import Shared.Entities.MusicEntity;
import Shared.Entities.PlaylistEntity;
import Shared.Enums.Status;
import Shared.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MusicPresentationController implements Initializable {
    private ClientManager client;
    private LoadManager loader;
    private final ObjectMapper mapper = new ObjectMapper();
    private MusicEntity music;
    private int musicIndex = 0;
    private ArrayList<MusicEntity> tracks;
    private boolean playAtInit = false;
    private PlayStatus playStatus = PlayStatus.finishAtEnd;
    private MediaPlayer mediaPlayer;
    private Media media;
    private Timer timer;
    private boolean running = false;
    private boolean finished = false;

    @FXML
    private Circle musicCover;
    @FXML
    private Label musicName, artist, album, genre, popularity, releaseDate, duration, likeError, addToPlsMessage, addComError, playError;
    @FXML
    private TextArea comments, lyrics;
    @FXML
    private Button likeMusic, addToPlaylist, leaveComment, previous, next, playMode;
    @FXML
    private VBox playlists;
    @FXML
    private TextField commentText;
    @FXML
    private ImageView likeImage, playModeImage, playPauseImage;
    @FXML
    private Slider volume;
    @FXML
    private ScrollPane playlistScrollPane;
    @FXML
    private ProgressBar musicProgressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mapper.registerModule(new JavaTimeModule());

        this.comments.setEditable(false);
        this.lyrics.setEditable(false);

        this.music = this.fullInfoMusic(this.tracks.get(this.musicIndex).getId());
        this.musicName.setText(this.music.getTitle());
        this.artist.setText(this.music.getArtist().getName());
        this.album.setText(this.music.getAlbum().getTitle());
        this.genre.setText(this.music.getGenre().getName());
        this.popularity.setText(Integer.toString(this.music.getPopularity()));
        this.releaseDate.setText(this.music.getReleaseDate().toString());
        this.duration.setText(this.calculateDuration());
        this.comments.setText(this.getComments());
        this.lyrics.setText(this.music.getLyric());

        try {
            Image image = new Image(new FileInputStream(this.client.downloadMusicCover(this.music.getId())));
            this.musicCover.setFill(new ImagePattern(image));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.doesUserLikedMusic(this.music.getId())) {
            try {
                Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\likeRed.png"));
                this.likeImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (this.playAtInit) this.playPause();
    }

    @FXML
    private void likeMusic() {
        this.invisibleMessages();
        if (this.doesUserLikedMusic(this.music.getId())) {
            if (this.disLike()) {
                try {
                    Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\like.png"));
                    this.likeImage.setImage(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                this.likeError.setVisible(true);
            }
        } else {
            if (this.like()) {
                try {
                    Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\likeRed.png"));
                    this.likeImage.setImage(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                this.likeError.setVisible(true);
            }
        }
    }

    @FXML
    private void selectPlaylist() {
        this.invisibleMessages();
        ArrayList<PlaylistEntity> userPlaylists = this.findUserPlaylists();
        if (userPlaylists.size() == 0) {
            this.addToPlsMessage.setText("First logIn!");
            this.addToPlsMessage.setVisible(true);
            return;
        } else {
            for (PlaylistEntity playlistEntity : userPlaylists) {
                Button playlistButton = new Button(playlistEntity.getTitle());
                playlistButton.setUserData(playlistEntity);
                playlistButton.setPrefHeight(30);
                playlistButton.setPrefWidth(1000);
                playlistButton.setOnAction(event -> {
                    PlaylistEntity playlist = (PlaylistEntity) playlistButton.getUserData();
                    this.addToPlaylist(playlist);
                });
                this.playlists.getChildren().add(playlistButton);
            }
        }
        this.playlistScrollPane.setVisible(true);
        this.playlists.setVisible(true);
    }

    @FXML
    private void leaveComment() {
        this.invisibleMessages();
        CommentEntity dto = new CommentEntity();
        dto.setUserId(this.client.getCurrentUserId());
        dto.setMusicId(this.music.getId());
        dto.setComment(this.commentText.getText());
        Response response = this.client.addCommentToMusic(dto);
        if (response.getStatus().equals(Status.successful)) {
            this.comments.appendText(this.commentText.getText());
            this.commentText.clear();
        } else {
            this.commentText.clear();
            this.addComError.setVisible(true);
        }
    }

    @FXML
    private void playPause() {
        this.invisibleMessages();
        if (this.running) {
            this.pauseTimer();
            this.mediaPlayer.pause();
            try {
                Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\play.png"));
                this.playPauseImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\pause.png"));
                this.playPauseImage.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (this.mediaPlayer != null) {
                mediaPlayer.setVolume(volume.getValue() * 0.01);
                if (this.finished) {
                    this.mediaPlayer = new MediaPlayer(this.media);
                    this.beginTimer();
                    this.mediaPlayer.play();
                } else {
                    this.continueTimer();
                    this.mediaPlayer.play();
                }
            } else {
                // download file
                FileDto fileDto = new FileDto();
                fileDto.setName(this.music.getTitle());
                fileDto.setId(this.music.getFile().getId());
                fileDto.setMemeType(this.music.getFile().getMemeType());
                String filePath = this.client.download(fileDto);
                if (filePath == null) {
                    this.playError.setText("Please first logIn!");
                    this.playError.setVisible(true);
                    return;
                }
                File musicFile = new File(filePath);
                this.media = new Media(musicFile.toURI().toString());
                this.mediaPlayer = new MediaPlayer(this.media);
                this.beginTimer();
                this.mediaPlayer.play();
                this.volume.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        mediaPlayer.setVolume(volume.getValue() * 0.01);
                    }
                });
            }
        }
    }

    @FXML
    private void next() {
        Stage stage = (Stage) this.musicName.getScene().getWindow();
        this.mediaPlayer.stop();
        this.timer.cancel();
        if (this.playStatus.equals(PlayStatus.shuffle)) {
            this.loader.loadMusicPresentationPage(stage, this.calculateMusicTurnInShuffle(), this.tracks, true, this.playStatus);
        } else {
            if (this.musicIndex < this.tracks.size() - 1) {
                this.loader.loadMusicPresentationPage(stage, this.musicIndex + 1, this.tracks, true, this.playStatus);
            } else if (this.musicIndex == this.tracks.size() - 1) {
                this.loader.loadMusicPresentationPage(stage, 0, this.tracks, true, this.playStatus);
            }
        }
    }

    @FXML
    private void previous() {
        Stage stage = (Stage) this.musicName.getScene().getWindow();
        this.mediaPlayer.stop();
        this.timer.cancel();
        if (this.playStatus.equals(PlayStatus.shuffle)) {
            this.loader.loadMusicPresentationPage(stage, this.calculateMusicTurnInShuffle(), this.tracks, true, this.playStatus);
        } else {
            if (this.musicIndex > 0) {
                this.loader.loadMusicPresentationPage(stage, this.musicIndex - 1, this.tracks, true, this.playStatus);
            } else if (this.musicIndex == 0) {
                this.loader.loadMusicPresentationPage(stage, this.tracks.size() - 1, this.tracks, true, this.playStatus);
            }
        }
    }

    @FXML
    private void playMode() {
        switch (this.playStatus) {
            case finishAtEnd:
                this.playStatus = PlayStatus.repeatOne;
                try {
                    Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\repeat one.png"));
                    this.playModeImage.setImage(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case repeatOne:
                this.playStatus = PlayStatus.consequence;
                try {
                    Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\consequence.png"));
                    this.playModeImage.setImage(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case consequence:
                this.playStatus = PlayStatus.shuffle;
                try {
                    Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\shuffle.png"));
                    this.playModeImage.setImage(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case shuffle:
                this.playStatus = PlayStatus.finishAtEnd;
                try {
                    Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\finishAtEnd.png"));
                    this.playModeImage.setImage(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setClient(ClientManager client) {
        this.client = client;
        this.loader = new LoadManager(this.client);
    }

    public void setMusicIndex(int musicIndex) {
        this.musicIndex = musicIndex;
    }

    public void setTracks(ArrayList<MusicEntity> tracks) {
        this.tracks = tracks;
    }

    public void setPlayAtInit(boolean playAtInit) {
        this.playAtInit = playAtInit;
    }

    public void setPlayStatus(PlayStatus playStatus) {
        this.playStatus = playStatus;
    }

    private void beginTimer() {
        this.running = true;
        this.finished = false;
        this.timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                musicProgressBar.setProgress(current / end);
                if (current / end == 1) {
                    cancelTimer();
                    switch (playStatus) {
                        case repeatOne:
                            playPause();
                            break;
                        case consequence:
                            next();
                            break;
                        case shuffle:
                            Stage stage = (Stage) musicName.getScene().getWindow();
                            loader.loadMusicPresentationPage(stage, calculateMusicTurnInShuffle(), tracks, true, playStatus);
                            break;
                    }
                }
            }
        };
        this.timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void cancelTimer() {
        this.running = false;
        this.finished = true;
        this.timer.cancel();
    }

    private void continueTimer() {
        this.running = true;
    }

    private void pauseTimer() {
        this.running = false;
    }

    private boolean like() {
        LikeMusicDto dto = new LikeMusicDto();
        dto.setId(this.music.getId());
        Response response = this.client.likeMusic(dto);
        if (response.getStatus().equals(Status.successful)){
            return true;
        } else {
            return false;
        }
    }

    private boolean disLike() {
        DislikeMusicDto dto = new DislikeMusicDto();
        dto.setId(this.music.getId());
        Response response = this.client.dislikeMusic(dto);
        if (response.getStatus().equals(Status.successful)){
            return true;
        } else {
            return false;
        }
    }

    private void addToPlaylist(PlaylistEntity playlist) {
        this.playlistScrollPane.setVisible(false);
        this.playlists.setVisible(false);

        AddMusicToPlaylistDto dto = new AddMusicToPlaylistDto();
        dto.setMusicId(this.music.getId());
        dto.setId(playlist.getId());
        Response response = this.client.addMusicToPlaylist(dto);
        if (response.getStatus().equals(Status.successful)) {
            this.addToPlsMessage.setText(this.music.getTitle() + " just added to " + playlist.getTitle());
            this.addToPlsMessage.setVisible(true);
        } else {
            this.addToPlsMessage.setText("Something went wrong!");
            this.addToPlsMessage.setVisible(true);
        }
    }

    private String calculateDuration() {
        String duration = "";
        int d = this.music.getDuration();
        int minutes = (int) Math.floor(d / 60);
        int seconds = d - (minutes * 60);
        duration += Integer.toString(minutes);
        duration += ":";
        duration += Integer.toString(seconds);
        return duration;
    }

    private String getComments() {
        String comments = "";
        for (CommentEntity comment: this.music.getComments()) {
            comments += comment.getComment();
            comments += "\n";
        }
        return comments;
    }

    private boolean doesUserLikedMusic(int musicId) {
        Response response = this.client.doesUserLikedMusic(musicId);
        return this.mapper.convertValue(response.getData(), boolean.class);
    }

    private ArrayList<PlaylistEntity> findUserPlaylists() {
        Response response = this.client.getUserPlaylists(this.client.getCurrentUserId());
        if (response.getStatus().equals(Status.successful)) {
            PlaylistEntity[] playlistArr = this.mapper.convertValue(response.getData(), PlaylistEntity[].class);
            return new ArrayList<PlaylistEntity>(Arrays.asList(playlistArr));
        }
        return new ArrayList<>();
    }

    private MusicEntity fullInfoMusic(int musicId) {
        FindOneMusicDto dto = new FindOneMusicDto();
        dto.setId(musicId);
        Response response = this.client.findOneMusic(dto);
        return this.mapper.convertValue(response.getData(), MusicEntity.class);
    }

    private int calculateMusicTurnInShuffle() {
        int max = this.tracks.size();
        if (max > 1) {
            while (true) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, max);
                if (randomNum != this.musicIndex)
                    return randomNum;
            }
        }
        return this.musicIndex;
    }

    private void invisibleMessages() {
        this.addToPlsMessage.setVisible(false);
        this.likeError.setVisible(false);
        this.addComError.setVisible(false);
        this.playlistScrollPane.setVisible(false);
        this.playlists.setVisible(false);
        this.playError.setVisible(false);
    }
}
