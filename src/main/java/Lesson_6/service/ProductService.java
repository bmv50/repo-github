package Lesson_6.service;

import Lesson_6.dto.Product;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import sun.plugin.util
        .PluginSysUtil;

public interface ProductService {

    @GET("products")
    Call<Product> getProductAll();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @POST("products")
    Call<Product> postProduct(@Body Product createProductRequest);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") int id);

    @PUT("products")
    Call<Product> putProduct(@Body Product createProductRequest);

    @GET("products")
    PluginSysUtil createProduct(Product product);
}