package Lesson_6.service;

import Lesson_6.dto.Product;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ErrorService {

    @GET ("/market/api/v1/categories/{id}")
    Call<Error> getCategoriesError (@Path ("id") int id);

    @POST("products")
    Call<Product> postProductError (@Body Product createProductRequest);
}