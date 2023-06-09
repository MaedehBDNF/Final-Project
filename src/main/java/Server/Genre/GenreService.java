package Server.Genre;

import Server.Config.DatabaseConfigDto;
import Shared.Enums.Error;
import Shared.Enums.Title;
import Shared.Response;
import java.util.ArrayList;

public class GenreService {
    GenreRepository genreRepository;

    public GenreService(DatabaseConfigDto config){
        this.genreRepository = new GenreRepository(config);
    }

    public GenreEntity addGenre(GenreEntity genre){
        return this.genreRepository.insertIntoTable(genre);
    }

    public Response findOneGenre(int id){
        Response response = new Response();
        response.setTitle(Title.findOneGenre);
        GenreEntity entity = this.genreRepository.findOne(id);
        if (entity.getId() != 0) {
            response.successful();
            response.setData(entity);
        } else {
            response.setError(Error.notFound);
        }
        return response;
    }

    public Response findAll(){
        Response response = new Response();
        response.setTitle(Title.findAllGenres);
        ArrayList<GenreEntity> entities = this.genreRepository.findAll();
        response.successful();
        response.setData(entities);
        return response;
    }

    public GenreEntity findByName(String name){
        return this.genreRepository.findByName(name);
    }

    public void close() {
        this.genreRepository.closeConnection();
    }
}
