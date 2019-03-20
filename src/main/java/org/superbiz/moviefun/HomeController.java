package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    public PlatformTransactionManager platformTransactionManagerForMovies;
    public PlatformTransactionManager platformTransactionManagerForAlbums;

    private MoviesBean moviesBean;
    private AlbumsBean albumsBean;
    private MovieFixtures movieFixtures;
    private AlbumFixtures albumFixtures;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures, PlatformTransactionManager platformTransactionManagerForMovies, PlatformTransactionManager platformTransactionManagerForAlbums) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.platformTransactionManagerForMovies = platformTransactionManagerForMovies;
        this.platformTransactionManagerForAlbums = platformTransactionManagerForAlbums;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        createMovies();

        createAlbums();
        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }

    public void createMovies(){
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManagerForMovies);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                    for (Movie movie : movieFixtures.load()) {
                        moviesBean.addMovie(movie);
                    }
            }
        });
    }

    public void createAlbums(){
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManagerForAlbums);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (Album album : albumFixtures.load()) {
                    albumsBean.addAlbum(album);
                }
            }
        });
    }
}
