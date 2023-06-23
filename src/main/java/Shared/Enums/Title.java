package Shared.Enums;

public enum Title {
    // User
    register,
    login,
    logOut,
    findOneUser,
    searchUser,
    followUser,
    followArtist,
    findUserLikedAlbums,
    getUserFriends,
    getUserFollowings,
    doesUserFollowedArtist,
    doesUserFollowedUser,
    // File
    upload,
    getFileInfo,
    download,
    // Genre
    findOneGenre,
    findAllGenres,
    // Artist
    findOneArtist,
    searchArtist,
    findArtistAlbums,
    // Album
    findOneAlbum,
    findAlbumSongs,
    searchAlbum,
    likeAlbum,
    doesUserLikedAlbum,
    // Music
    findOneMusic,
    searchMusic,
    likeMusic,
    dislikeMusic,
    addCommentOnMusic,
    // Playlist
    createPlaylist,
    findOnePlaylist,
    findAllUserPlaylists,
    searchPlaylist,
    likePlayList,
    addPlaylist,
    addMusicToPlaylist,
    removeMusicFromPlaylist,
    changeMusicOrderInPlaylist,
    doesUserLikedPlaylist,
    doesUserAddedPlaylist,
    // All
    completeSearch,
    // Exit
    exit
}
