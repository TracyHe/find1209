package com.hp.autonomy.frontend.find.core.rating;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(RatingController.SEARCH_PATH)
public class RatingController {
    public static final String SEARCH_PATH = "/api/public";
    public RatingService ratingService;

    @RequestMapping(value = "/rating", params="docreferenceid", method = RequestMethod.GET)
    @ResponseBody
    public float getAverageRatingByDocId(@RequestParam("docreferenceid") String docreferenceid)
    {
        ratingService = new RatingService();
        return ratingService.getAverageRating(docreferenceid);
    }

    @RequestMapping(value = "/rating",  method = RequestMethod.POST)
    @ResponseBody

    public ResponseEntity<String> CreateUserRating(@RequestBody Rating ur)
    {
        ratingService = new RatingService();

        int id = ratingService.CreateUserRating(ur);

        if(id==0){
            return new ResponseEntity<>(
                    "Duplicated Ratings",
                    HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(
                    "Rating Successfully",  HttpStatus.CREATED);
        }
    }

}
