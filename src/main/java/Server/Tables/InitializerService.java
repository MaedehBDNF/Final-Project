package Server.Tables;

import Server.Album.AlbumService;
import Server.Artist.ArtistService;
import Server.Config.DatabaseConfigDto;
import Server.FileManager.FileService;
import Shared.Entities.GenreEntity;
import Server.Genre.GenreService;
import Server.Music.MusicService;
import Server.User.UserService;
import Shared.Dto.Album.CreatorAlbumDto;
import Shared.Dto.Artist.CreateArtistDto;
import Shared.Dto.File.FileDto;
import Shared.Dto.File.UploadDto;
import Shared.Dto.Music.MusicDto;
import Shared.Enums.UploadType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class InitializerService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final MusicService musicService;
    private final GenreService genreService;
    private final FileService fileService;
    private final String dir = System.getProperty("user.dir") + "\\src\\main\\java\\Server\\Resources";

    private final HashMap<String, Integer> genres = new HashMap<>();
    private final HashMap<String, Integer> artists = new HashMap<>();
    private final HashMap<String, Integer> albums = new HashMap<>();

    public InitializerService(DatabaseConfigDto config) {
        new InitializerRepository(config);
        this.userService = new UserService(config);
        this.albumService = new AlbumService(config);
        this.artistService = new ArtistService(config);
        this.musicService = new MusicService(config);
        this.genreService = new GenreService(config);
        this.fileService = new FileService(config);
        this.makeResourcesDirectory();
        this.seedData();
        this.close();
    }

    private void makeResourcesDirectory() {
        boolean success;
        File directory = new File(dir);
        if (!directory.exists()) {
            System.out.println("Resources directory not exists, creating now");
            success = directory.mkdir();
            if (success) {
                System.out.printf("Successfully created new directory : %s%n", dir);
            } else {
                System.out.printf("Failed to create new directory: %s%n", dir);
            }
        }
    }

    private void seedData() {
        if (this.doesSeedDataExecute()) return;
        this.seedGenres();
        this.seedArtists();
        this.seedAlbums();
        this.seedMusics();
    }

    private void close() {
        System.out.println("Closing initializer service.");
        this.userService.close();
        this.albumService.close();
        this.artistService.close();
        this.musicService.close();
        this.genreService.close();
        this.fileService.lightClose();
    }

    private void seedGenres() {
        GenreEntity genreEntity = new GenreEntity();

        genreEntity.setName("Hip hop");
        genreEntity.setDescription("Hip hop music or hip-hop music, also known as rap music and formerly known as disco rap.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Jazz");
        genreEntity.setDescription("This genre has been recognized as a major form of musical expression in traditional and popular music.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Rock");
        genreEntity.setDescription("Rock music is a broad genre of popular music that originated as \"rock and roll\" in the United States.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Pop");
        genreEntity.setDescription("Pop music is a genre of popular music that originated in its modern form during the mid-1950s in the United States.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Soul");
        genreEntity.setDescription("Soul music is a popular music genre that originated in the African American community.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Blues");
        genreEntity.setDescription("Blues is a music genre and musical form which originated in the Deep South of the United States around the 1860s.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Metal");
        genreEntity.setDescription("Heavy metal is a genre of rock music that developed in the late 1960s and early 1970s, largely in the United Kingdom and United States.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Classical music");
        genreEntity.setDescription("Classical music generally refers to the art music of the Western world, considered to be distinct from Western folk music or popular music traditions.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Pop rock");
        genreEntity.setDescription("Pop rock is a fusion genre with an emphasis on professional songwriting and recording craft, and less emphasis on attitude than rock music.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());

        genreEntity.setName("Gospel");
        genreEntity.setDescription("Gospel music is a traditional genre of Christian music, and a cornerstone of Christian media.");
        this.genres.put(genreEntity.getName(), this.genreService.addGenre(genreEntity).getId());
    }

    private void seedArtists() {
        CreateArtistDto createArtistDto = new CreateArtistDto();
        UploadDto uploadDto = new UploadDto();
        ArrayList<String> socialLinks = new ArrayList<>();

        createArtistDto.setName("Eminem");
        createArtistDto.setGenreId(this.genres.get("Hip hop"));
        createArtistDto.setBiography("Marshall Bruce Mathers III, known professionally as Eminem, is an American rapper, songwriter, and record producer. \n" +
                "He is credited with popularizing hip hop in middle America and is critically acclaimed as one of the greatest rappers of all time.");
        uploadDto.setName("Eminem");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.artistProfilePicture);
        createArtistDto.setProfilePictureId(makeFile(uploadDto, dir + "\\Eminem.jpg"));
        socialLinks.add("https://www.instagram.com/eminem/?hl=en");
        socialLinks.add("https://twitter.com/Eminem");
        createArtistDto.setSocialMediaLinks(socialLinks);
        artists.put("Eminem" ,this.artistService.addArtist(createArtistDto).getId());
        socialLinks.clear();

        createArtistDto.setName("Drake");
        createArtistDto.setGenreId(this.genres.get("Hip hop"));
        createArtistDto.setBiography("Aubrey Drake Graham is a Canadian rapper, singer, and songwriter. \n" +
                "An influential figure in contemporary popular music, Drake has been credited for popularizing singing and R&B sensibilities in hip hop.");
        uploadDto.setName("Drake");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.artistProfilePicture);
        createArtistDto.setProfilePictureId(makeFile(uploadDto, dir + "\\Drake.jpg"));
        socialLinks.add("https://twitter.com/Drake");
        socialLinks.add("https://www.facebook.com/Drake/");
        createArtistDto.setSocialMediaLinks(socialLinks);
        artists.put("Drake" ,this.artistService.addArtist(createArtistDto).getId());
        socialLinks.clear();

        createArtistDto.setName("Norah Jones");
        createArtistDto.setGenreId(this.genres.get("Jazz"));
        createArtistDto.setBiography("Norah Jones is an American singer, songwriter, and pianist. \n" +
                "She has won several awards for her music and, as of 2023, had sold more than 50 million records worldwide. \n" +
                "Billboard named her the top jazz artist of the 2000's decade.");
        uploadDto.setName("Norah Jones");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.artistProfilePicture);
        createArtistDto.setProfilePictureId(makeFile(uploadDto, dir + "\\Norah Jones.jpg"));
        socialLinks.add("https://www.instagram.com/norahjones/?hl=en");
        socialLinks.add("https://twitter.com/NorahJones");
        createArtistDto.setSocialMediaLinks(socialLinks);
        artists.put("Norah Jones" ,this.artistService.addArtist(createArtistDto).getId());
        socialLinks.clear();

        createArtistDto.setName("Adele");
        createArtistDto.setGenreId(this.genres.get("Pop"));
        createArtistDto.setBiography("Adele Laurie Blue Adkins MBE is an English singer-songwriter.\n" +
                "After graduating in arts from the BRIT School in 2006, Adele signed a record deal with XL Recordings.\n" +
                "Her debut album, 19, was released in 2008 and spawned the UK top-five singles \"Chasing Pavements\" and \"Make You Feel My Love\".");
        uploadDto.setName("Adele");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.artistProfilePicture);
        createArtistDto.setProfilePictureId(makeFile(uploadDto, dir + "\\Adele.jpg"));
        socialLinks.add("https://www.instagram.com/adele/?hl=en");
        socialLinks.add("https://twitter.com/Adele");
        createArtistDto.setSocialMediaLinks(socialLinks);
        artists.put("Adele" ,this.artistService.addArtist(createArtistDto).getId());
        socialLinks.clear();

        createArtistDto.setName("Taylor Swift");
        createArtistDto.setGenreId(this.genres.get("Pop"));
        createArtistDto.setBiography("Taylor Alison Swift is an American singer-songwriter.\n" +
                "Recognized for her genre-spanning discography, songwriting, and artistic reinventions, \n" +
                "Swift is a prominent cultural figure who has been cited as an influence on a generation of music artists.");
        uploadDto.setName("Taylor Swift");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.artistProfilePicture);
        createArtistDto.setProfilePictureId(makeFile(uploadDto, dir + "\\Taylor Swift.jpg"));
        socialLinks.add("https://www.instagram.com/taylorswift/?hl=en");
        createArtistDto.setSocialMediaLinks(socialLinks);
        artists.put("Taylor Swift" ,this.artistService.addArtist(createArtistDto).getId());
        socialLinks.clear();

        createArtistDto.setName("Justin Bieber");
        createArtistDto.setGenreId(this.genres.get("Pop"));
        createArtistDto.setBiography("Justin Drew Bieber is a Canadian singer.\n" +
                "He is recognized for his genre-melding musicianship and global influence in modern-day popular music.");
        uploadDto.setName("Justin Bieber");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.artistProfilePicture);
        createArtistDto.setProfilePictureId(makeFile(uploadDto, dir + "\\Justin Bieber.jpg"));
        socialLinks.add("https://www.instagram.com/justinbieber/");
        socialLinks.add("hhttps://twitter.com/justinbieber");
        createArtistDto.setSocialMediaLinks(socialLinks);
        artists.put("Justin Bieber" ,this.artistService.addArtist(createArtistDto).getId());
        socialLinks.clear();
    }

    private void seedAlbums() {
        CreatorAlbumDto creatorAlbumDto = new CreatorAlbumDto();
        UploadDto uploadDto = new UploadDto();

        creatorAlbumDto.setTitle("Justice");
        creatorAlbumDto.setArtistId(this.artists.get("Justin Bieber"));
        creatorAlbumDto.setGenreId(this.genres.get("Pop"));
        creatorAlbumDto.setReleaseDate(LocalDate.of(2021, 3, 19));
        uploadDto.setName("Justice");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.albumCover);
        creatorAlbumDto.setCoverId(makeFile(uploadDto, dir + "\\Justice.jpg"));
        this.albums.put("Justice", this.albumService.addAlbum(creatorAlbumDto).getId());

        creatorAlbumDto.setTitle("21");
        creatorAlbumDto.setArtistId(this.artists.get("Adele"));
        creatorAlbumDto.setGenreId(this.genres.get("Pop"));
        creatorAlbumDto.setReleaseDate(LocalDate.of(2011, 1, 24));
        uploadDto.setName("21");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.albumCover);
        creatorAlbumDto.setCoverId(makeFile(uploadDto, dir + "\\21.jpg"));
        this.albums.put("21", this.albumService.addAlbum(creatorAlbumDto).getId());

        creatorAlbumDto.setTitle("Recovery");
        creatorAlbumDto.setArtistId(this.artists.get("Eminem"));
        creatorAlbumDto.setGenreId(this.genres.get("Hip hop"));
        creatorAlbumDto.setReleaseDate(LocalDate.of(2010, 6, 18));
        uploadDto.setName("Recovery");
        uploadDto.setMemeType("jpg");
        uploadDto.setUploadType(UploadType.albumCover);
        creatorAlbumDto.setCoverId(makeFile(uploadDto, dir + "\\Recovery.jpg"));
        this.albums.put("Recovery", this.albumService.addAlbum(creatorAlbumDto).getId());
    }

    private void seedMusics() {
        MusicDto musicDto = new MusicDto();
        UploadDto uploadDto = new UploadDto();

        musicDto.setTitle("Someone like you");
        musicDto.setArtistId(this.artists.get("Adele"));
        musicDto.setGenreId(this.genres.get("Pop"));
        musicDto.setAlbumId(this.albums.get("21"));
        musicDto.setDuration(287);
        musicDto.setReleaseDate(LocalDate.of(2011, 1, 24));
        musicDto.setLyric("I heard that you're settled down\n" +
                "That you found a girl and you're married now\n" +
                "I heard that your dreams came true\n" +
                "Guess she gave you things, I didn't give to you\n" +
                "Old friend, why are you so shy?\n" +
                "Ain't like you to hold back or hide from the light\n" +
                "I hate to turn up out of the blue, uninvited\n" +
                "But I couldn't stay away, I couldn't fight it\n" +
                "I had hoped you'd see my face\n" +
                "And that you'd be reminded that for me, it isn't over\n" +
                "Never mind, I'll find someone like you\n" +
                "I wish nothing but the best for you, too\n" +
                "\"Don't forget me, \" I beg\n" +
                "I remember you said\n" +
                "\"Sometimes it lasts in love, but sometimes it hurts instead\"\n" +
                "\"Sometimes it lasts in love, but sometimes it hurts instead\"\n" +
                "You know how the time flies\n" +
                "Only yesterday was the time of our lives\n" +
                "We were born and raised in a summer haze\n" +
                "Bound by the surprise of our glory days\n" +
                "I hate to turn up out of the blue, uninvited\n" +
                "But I couldn't stay away, I couldn't fight it\n" +
                "I had hoped you'd see my face\n" +
                "And that you'd be reminded that for me, it isn't over\n" +
                "Never mind, I'll find someone like you\n" +
                "I wish nothing but the best for you, too\n" +
                "\"Don't forget me, \" I begged\n" +
                "I remember you said\n" +
                "\"Sometimes it lasts in love, but sometimes it hurts instead\"\n" +
                "Nothing compares, no worries or cares\n" +
                "Regrets and mistakes, they're memories made\n" +
                "Who would have known how bittersweet this would taste?\n" +
                "Never mind, I'll find someone like you\n" +
                "I wish nothing but the best for you\n" +
                "\"Don't forget me, \" I beg\n" +
                "I remember you said\n" +
                "\"Sometimes it lasts in love, but sometimes it hurts instead\"\n" +
                "Never mind, I'll find someone like you\n" +
                "I wish nothing but the best for you, too\n" +
                "\"Don't forget me, \" I begged\n" +
                "I remember you said\n" +
                "\"Sometimes it lasts in love, but sometimes it hurts instead\"\n" +
                "\"Sometimes it lasts in love, but sometimes it hurts instead\"");
        uploadDto.setName("Someone-Like-You-320");
        uploadDto.setMemeType("mp3");
        uploadDto.setUploadType(UploadType.musicFile);
        musicDto.setFileId(this.makeFile(uploadDto, dir + "\\Someone-Like-You-320.mp3"));
        this.musicService.addMusic(musicDto);


        musicDto.setTitle("Rolling in the Deep");
        musicDto.setArtistId(this.artists.get("Adele"));
        musicDto.setGenreId(this.genres.get("Pop"));
        musicDto.setAlbumId(this.albums.get("21"));
        musicDto.setDuration(229);
        musicDto.setReleaseDate(LocalDate.of(2011, 1, 24));
        musicDto.setLyric("There's a fire starting in my heart\n" +
                "Reaching a fever pitch, it's bringing me out the dark\n" +
                "Finally I can see you crystal clear\n" +
                "Go ahead and sell me out and I'll lay your ship bare\n" +
                "See how I'll leave with every piece of you\n" +
                "Don't underestimate the things that I will do\n" +
                "There's a fire starting in my heart\n" +
                "Reaching a fever pitch and it's bringing me out the dark\n" +
                "The scars of your love remind me of us\n" +
                "They keep me thinking that we almost had it all\n" +
                "The scars of your love they leave me breathless\n" +
                "I can't help feeling\n" +
                "We could've had it all (you're gonna wish you)\n" +
                "(Never had met me)\n" +
                "Rolling in the deep (tears are gonna fall)\n" +
                "(Rolling in the deep)\n" +
                "You had my heart inside (you're gonna wish you)\n" +
                "Of your hands (never had met me)\n" +
                "And you played it (tears are gonna fall)\n" +
                "To the beat (rolling in the deep)\n" +
                "Baby, I have no story to be told\n" +
                "But I've heard one on you, now I'm gonna make your head burn\n" +
                "Think of me in the depths of your despair\n" +
                "Make a home down there, as mine sure won't be shared\n" +
                "The scars of your love (never had met me)\n" +
                "Remind me of us (tears are gonna fall)\n" +
                "They keep me thinking (rolling in the deep)\n" +
                "That we almost had it all (you're gonna wish you)\n" +
                "The scars of your love (never had met me)\n" +
                "They leave me breathless (tears are gonna fall)\n" +
                "I can't help feeling (rolling in the deep)\n" +
                "We could've had it all (you're gonna wish you)\n" +
                "(Never had met me)\n" +
                "Rolling in the deep (tears are gonna fall)\n" +
                "(Rolling in the deep)\n" +
                "You had my heart inside (you're gonna wish you)\n" +
                "Of your hands (never had met me)\n" +
                "And you played it (tears are gonna fall)\n" +
                "To the beat (rolling in the deep)\n" +
                "We could've had it all\n" +
                "Rolling in the deep\n" +
                "You had my heart inside of your hand\n" +
                "But you played it with a beating\n" +
                "Throw your soul through every open door (whoa)\n" +
                "Count your blessings to find what you look for (whoa)\n" +
                "Turn my sorrow into treasured gold (whoa)\n" +
                "You pay me back in kind and reap just what you've sown\n" +
                "We could've had it all (tears are gonna fall, rolling in the deep)\n" +
                "We could've had it all (you're gonna wish you never had met me)\n" +
                "It all, it all, it all (tears are gonna fall, rolling in the deep)\n" +
                "We could've had it all (you're gonna wish you)\n" +
                "(Never had met me)\n" +
                "Rolling in the deep (tears are gonna fall)\n" +
                "(Rolling in the deep)\n" +
                "You had my heart inside (you're gonna wish you)\n" +
                "Of your hands (never had met me)\n" +
                "And you played it (tears are gonna fall)\n" +
                "To the beat (rolling in the deep)\n" +
                "Could've had it all (you're gonna wish you)\n" +
                "(Never had met me)\n" +
                "Rolling in the deep (tears are gonna fall)\n" +
                "(Rolling in the deep)\n" +
                "You had my heart inside (you're gonna wish you)\n" +
                "Of your hands (never had met me)\n" +
                "But you played it, you played it, you played it\n" +
                "You played it to the beat");
        uploadDto.setName("Rolling in the Deep");
        uploadDto.setMemeType("mp3");
        uploadDto.setUploadType(UploadType.musicFile);
        musicDto.setFileId(this.makeFile(uploadDto, dir + "\\Rolling in the Deep.mp3"));
        this.musicService.addMusic(musicDto);

        musicDto.setTitle("Lonely_320");
        musicDto.setArtistId(this.artists.get("Justin Bieber"));
        musicDto.setGenreId(this.genres.get("Pop"));
        musicDto.setAlbumId(this.albums.get("Justice"));
        musicDto.setDuration(157);
        musicDto.setReleaseDate(LocalDate.of(2021, 3, 19));
        musicDto.setLyric("Everybody knows my name now\n" +
                "But somethin' 'bout it still feels strange\n" +
                "Like lookin' in a mirror, tryna steady yourself\n" +
                "And seein' somebody else\n" +
                "And everything is not the same now\n" +
                "It feels like all our lives have changed\n" +
                "Maybe when I'm older, it'll all calm down\n" +
                "But it's killin' me now\n" +
                "What if you had it all\n" +
                "But nobody to call?\n" +
                "Maybe then, you'd know me\n" +
                "'Cause I've had everything\n" +
                "But no one's listening\n" +
                "And that's just fuckin' lonely\n" +
                "I'm so lonely\n" +
                "Lonely\n" +
                "Everybody knows my past now\n" +
                "Like my house was always made of glass\n" +
                "And maybe that's the price you pay\n" +
                "For the money and fame at an early age\n" +
                "And everybody saw me sick\n" +
                "And it felt like no one gave a shit\n" +
                "They criticized the things I did\n" +
                "As an idiot kid\n" +
                "What if you had it all\n" +
                "But nobody to call?\n" +
                "Maybe then, you'd know me\n" +
                "'Cause I've had everything\n" +
                "But no one's listening\n" +
                "And that's just fuckin' lonely\n" +
                "I'm so lonely\n" +
                "Lonely\n" +
                "I'm so lonely\n" +
                "Lonely");
        uploadDto.setName("Lonely_320");
        uploadDto.setMemeType("mp3");
        uploadDto.setUploadType(UploadType.musicFile);
        musicDto.setFileId(this.makeFile(uploadDto, dir + "\\Lonely_320.mp3"));
        this.musicService.addMusic(musicDto);

        musicDto.setTitle("On Fire");
        musicDto.setArtistId(this.artists.get("Eminem"));
        musicDto.setGenreId(this.genres.get("Hip hop"));
        musicDto.setAlbumId(this.albums.get("Recovery"));
        musicDto.setDuration(213);
        musicDto.setReleaseDate(LocalDate.of(2010, 6, 18));
        musicDto.setLyric("Yeah, ya know? Critics man\n" +
                "Critics never got nothin' nice to say, man\n" +
                "You know the one thing I notice about critics, man?\n" +
                "Is critics never ask me how my day went\n" +
                "Well, I'ma tell 'em\n" +
                "Yesterday my dog died, I hog tied a ho, tied her in a bow\n" +
                "Said next time you blow up try to spit a flow\n" +
                "You wanna criticize dog try a little mo'\n" +
                "I'm so tired of this I could blow, fire in the hole\n" +
                "I'm fired up so fire up the lighter and the 'dro\n" +
                "Better hold on a little tighter here I go\n" +
                "Flows tighter, hot headed as ghost rider\n" +
                "Cold hearted as spiderman throwin' a spider in the snow\n" +
                "So ya better get to blowin in flow rider\n" +
                "Inside of a low rider with no tires in the hole\n" +
                "Why am I like this? Why is winter cold?\n" +
                "Why is it when I talk, I'm so biased to the hoes?\n" +
                "Listen dog, Christmas is off, this is as soft as it gets\n" +
                "This isn't gob this is a blister in the salt\n" +
                "Those are your wounds this is the salt, so get lost\n" +
                "Shit dissin' me is just like pissin' off the Wizard of Oz\n" +
                "Wrap a lizard in gauze, beat you in the jaws with it\n" +
                "Grab the scissors and saws\n" +
                "And cut out your livers gizzards and balls\n" +
                "Throw you in the middle of the ocean in the blizzard with jaws\n" +
                "So sip piss like sizzurp through a straw\n" +
                "Then describe how it tasted like dessert to us all\n" +
                "Got the gall to make Chris piss in his draws\n" +
                "Ticklin' him go to his grave, skip him and visit his dog\n" +
                "You're on fire\n" +
                "That's how ya know your on a roll\n" +
                "'Cause when you hot it's like your burnin' up everyone else's cold\n" +
                "You're on fire\n" +
                "Man, I'm so fuckin' sick, I got ambulances pullin' me over and shit\n" +
                "You're on fire\n" +
                "Ya need to stop drop and roll 'cause when you say the shit\n" +
                "To give the whole hip hop shop the blow\n" +
                "You're on fire, yeah, you're on fire, yeah\n" +
                "I just wrote a bullshit hook in between two long ass verses\n" +
                "If you mistook the for a song, look\n" +
                "This ain't a song it's a warnin' to Brooke Hogan and David Cook\n" +
                "That the crook just took over so book\n" +
                "Run as fast as you can, stop writin' and kill it\n" +
                "I'm lightning in a skillet, you're a fuckin' flash in a pan\n" +
                "I pop up you bitches scatter like hot grease splashin' a fan\n" +
                "Mr Mathers is the man\n" +
                "Yeah, I'm pissed but I would rather take this energy\n" +
                "And stash it in a can, come back and whip your ass with it again\n" +
                "Salivas like sulfuric acid in your hand it'll eat through\n" +
                "Anything metal the ass of Iron Man\n" +
                "Turn him into plastic so for you to think\n" +
                "That you could stand a fuckin' chance is assanine\n" +
                "Yeah, ask Denaun man, hit a blind man with a coloring book\n" +
                "And told him color inside the lines or get hit widda fine crayon\n" +
                "Fuck it I ain't playin', pull up in a van and hop out\n" +
                "At a homeless man holdin' a sign sayin'\n" +
                "Vietnam vet, I'm out my fuckin' mind, man\n" +
                "Kick over the can beat his ass and leave him nine grand\n" +
                "So if I seem a little mean to you\n" +
                "This ain't savage you ain't never seen the brew\n" +
                "You wanna get graphic we can go the scenic route\n" +
                "You couldn't make a belemic puke\n" +
                "On a piece of fuckin' corn and peanut boo\n" +
                "Sayin' you sick, quit playin' you prick don't nobody care\n" +
                "Then why the fuck am I yellin' at air\n" +
                "I ain't even talkin' to no one 'cause ain't nobody there\n" +
                "Nobody will fuckin' test me 'cause these hos won't even dare\n" +
                "I'm wastin' punchlines but I got so many to spare\n" +
                "I just thought of another one that might go here\n" +
                "Naw, don't waste it save it, psycho, yeah\n" +
                "Plus you gotta rewrite those lines that you said about Michael's hair\n" +
                "You're on fire\n" +
                "That's how ya know your on a roll\n" +
                "'Cause when you hot it's like your burnin' up everyone else's cold\n" +
                "You're on fire\n" +
                "Man, I'm so hot my motherfuckin' firetrucks on fire, homie\n" +
                "You're on fire\n" +
                "Ya need to stop drop and roll 'cause when you say the shit\n" +
                "To give the whole hip hop shop the blow\n" +
                "You're on fire, yeah, your on fire\n" +
                "You're on fire");
        uploadDto.setName("On Fire");
        uploadDto.setMemeType("mp3");
        uploadDto.setUploadType(UploadType.musicFile);
        musicDto.setFileId(this.makeFile(uploadDto, dir + "\\On Fire.mp3"));
        this.musicService.addMusic(musicDto);
    }

    private int makeFile(UploadDto uploadDto, String direction) {
        return this.mapper.convertValue(this.fileService.uploadFile(uploadDto, direction).getData(), FileDto.class).getId();
    }

    private boolean doesSeedDataExecute() {
        return this.mapper.convertValue(this.genreService.findAll().getData(), GenreEntity[].class).length > 0;
    }
}
