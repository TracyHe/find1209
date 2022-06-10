package com.hp.autonomy.frontend.find.core.rating;


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
    public int CreateUserRating(@RequestBody Rating ur)
    {
        ratingService = new RatingService();

        return ratingService.CreateUserRating(ur);
    }
}
