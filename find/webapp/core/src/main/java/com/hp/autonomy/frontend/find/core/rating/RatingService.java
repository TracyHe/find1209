package com.hp.autonomy.frontend.find.core.rating;

import java.sql.*;

public class RatingService {
    private String url = "jdbc:postgresql://localhost:5432/rating";
    private String username = "postgres";
    private String password = "postgres";
    private Connection connection = null;
    public float getAverageRating(String docreferenceid){
        float rat = 0;
        PreparedStatement prsmt = null;
        try {

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url,username,password);
            String query = "select AVG(rating) as averageRating from ratings where docreferenceid = ?";
            prsmt = connection.prepareStatement(query);
            prsmt.setString(1,docreferenceid);
            ResultSet rs = prsmt.executeQuery();
            while (rs.next()){
                rat = rs.getFloat("averageRating");
                System.out.println(rat);
            }
            rs.close();
            prsmt.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rat;
    }

    private boolean RatingExist(String url, String username)
    {
        boolean exist = false;
        PreparedStatement prsmt = null;
        String query = "select count(id) as count from ratings where docreferenceid = ? and username = ?";
        try {

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url,username,password);
            prsmt = connection.prepareStatement(query);
            prsmt.setString(1,url);
            prsmt.setString(2,username);
            ResultSet rs = prsmt.executeQuery();
            while (rs.next()){
                int rat = rs.getInt("count");
                if (rat>0) {
                    exist = true;
                    return exist;
                }
            }

            rs.close();
            prsmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return exist;

    }
    public int CreateUserRating(Rating uRating)
    {
        boolean ratingExit = this.RatingExist(uRating.getDocreferenceid(),uRating.getUsername());
        int candidatedId = 0;
        if(ratingExit) {
            return candidatedId;
        }else {

            PreparedStatement prsmt = null;

            ResultSet rs = null;
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, username, password);

                String query = "INSERT INTO ratings (docreferenceid, username, rating) VALUES(?, ?, ?)";
                prsmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                prsmt.setString(1, uRating.getDocreferenceid());
                prsmt.setString(2, uRating.getUsername());
                prsmt.setFloat(3, uRating.getRating());
                int rowAffected = prsmt.executeUpdate();
                if (rowAffected == 1) {
                    rs = prsmt.getGeneratedKeys();
                    if (rs.next())

                        candidatedId = rs.getInt("id");

                }
                rs.close();
                prsmt.close();
                connection.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return candidatedId;
    }
}
