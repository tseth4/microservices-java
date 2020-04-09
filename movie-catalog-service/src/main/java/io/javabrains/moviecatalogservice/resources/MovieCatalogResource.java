package io.javabrains.moviecatalogservice.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	// give me all the movies watches and details
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
		
		// webclient is in the reactive space of the springboot ecosystem	
		
		UserRating ratings = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/" + userId, UserRating.class);
		

		
		return ratings.getUserRating().stream().map(rating -> {
//			Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
			
			Movie movie = webClientBuilder.build() // webclient build patter and giving client
				.get() // chaining mechanism to build upon this.  its a get method
				.uri("http://localhost:8082/movies/" + rating.getMovieId()) // where do you want the request to be made
				.retrieve() // go fetch
				.bodyToMono(Movie.class) // whatever body you get back convert it into an instance of thie movie class. Mono is reactive way of promise
				.block(); // tells that mono is fullfilled
			
			
			return new CatalogItem(movie.getName(), "Desc", rating.getRating());
	})
	.collect(Collectors.toList());
		

//		ratings.stream().map(rating -> {
//			new CatalogItem
//		}).collect()
		
		//get all rated movie IDs
		// for each movie ID, call movie info service and get details
		// put them all together
		
	}
}
