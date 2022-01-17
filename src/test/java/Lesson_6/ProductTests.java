package Lesson_6;

import Lesson_6.db.dao.CategoriesMapper;
import Lesson_6.db.dao.ProductsMapper;
import Lesson_6.db.model.Categories;
import Lesson_6.db.model.Products;
import Lesson_6.dto.Product;
import Lesson_6.enums.CategoryType;
import Lesson_6.service.ProductService;
import Lesson_6.util.DbUtils;
import Lesson_6.util.RetrofitUtils;
import Lesson_6.util.TestDataSet;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.val;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;


public class ProductTests {
    Integer productId;
    Faker faker = new Faker();
    static ProductService productService;
    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;
    Product product;
    public static Logger logger = LoggerFactory.getLogger(ProductTests.class);

    @SneakyThrows
    @BeforeAll
    static void beforeAll() throws MalformedURLException {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
        productsMapper = DbUtils.getProductsMapper();
        categoriesMapper = DbUtils.getCategoriesMapper();
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withCategoryTitle(CategoryType.FOOD.title)
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());
    }

    @SneakyThrows
    @DisplayName("Get Sugar Product Test")
    @Test
    void getSugarProductTest() throws IOException {
        Response<Product> response =
                productService.getProduct(Math.toIntExact(TestDataSet.PRODUCT_SUGAR_ID))
                        .execute();
        Product product = response.body();

        Products dbProduct = productsMapper.selectByPrimaryKey(Math.toIntExact(TestDataSet.PRODUCT_SUGAR_ID));
        Categories dbCategory = categoriesMapper.selectByPrimaryKey(dbProduct.getCategory_id().intValue());

        assertThat(response.isSuccessful()).isTrue();
        assertThat(product.getId()).isEqualTo(dbProduct.getId());
        assertThat(product.getTitle()).isEqualTo(dbProduct.getTitle());
        assertThat(product.getCategoryTitle()).isEqualTo(dbCategory.getTitle());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Get Just Created Product Test")
    @Test
    void getJustCreatedProductTest() throws IOException {
        // подготовка теста - создание нового продукта
        val createResponse =
                (Response<Product>) productService.createProduct(product)
                        .execute();
        productId = createResponse.body().getId();

        // тест
        Response<Product> getResponse = productService.getProduct(productId)
                .execute();
        Product gotProduct = getResponse.body();

        Products dbProduct = productsMapper.selectByPrimaryKey(productId);
        Categories dbCategory = categoriesMapper.selectByPrimaryKey(dbProduct.getCategory_id().intValue());

        assertThat(getResponse.isSuccessful()).isTrue();
        assertThat(gotProduct.getId()).isEqualTo(dbProduct.getId());
        assertThat(gotProduct.getTitle()).isEqualTo(dbProduct.getTitle());
        assertThat(gotProduct.getCategoryTitle()).isEqualTo(dbCategory.getTitle());
        assertThat(gotProduct.getPrice()).isEqualTo(dbProduct.getPrice());

        RetrofitUtils.createRequestAttachment(getResponse.raw().request().toString());
        RetrofitUtils.createResponseAttachment(getResponse.raw().toString());
    }
    @SneakyThrows
    @DisplayName("Create New Product Test")
    @Test
    void createNewProductTest() throws IOException {
        Response<Product> response =
                (Response<Product>) productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();
    }

    @SneakyThrows
    @DisplayName("Create New Product (Negative Test)")
    @Test
    void createNewProductNegativeTest() {
        Response<Product> response =
                productService.createProduct(product.withId(555))
                        .execute();
//        productId = Objects.requireNonNull(response.body()).getId();
        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Id must be null for new entity");

        }
        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Get Product Zero Id (Negative Test)")
    @Test
    void getProductZeroIdNegativeTest() throws IOException {
        Response<Product> response = productService.getProduct(0)
                .execute();

        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find product with id: 0");
        }
        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Get Product Minus Id (Negative Test)")
    @Test
    void getProductMinusIdNegativeTest() throws IOException {
        Response<Product> response = productService.getProduct(-2)
                .execute();

        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find product with id: -2");
        }
        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }
    @SneakyThrows
    @DisplayName("Get Product MAX Id (Negative Test)")
    @Test
    void getProductMaxIdNegativeTest() throws IOException {
        Response<Product> response = productService.getProduct((int) Long.MAX_VALUE)
                .execute();

        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find product with id: 9223372036854775807");
        }
        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product Zero Id (Negative Test)")
    @Test
    void putProductZeroNegativeTest() throws IOException {
        Product zeroProduct = new Product()
                .withId(0)
                .withCategoryTitle(CategoryType.FOOD.title)
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());

        Response<Product> response = productService.putProduct(zeroProduct)
                .execute();

        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Product with id: 0 doesn't exist");
        }
        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product MIN Id (Negative Test)")
    @Test
    void putProductMinIdNegativeTest() throws IOException {
        Product zeroProduct = new Product()
                .withId((int) Long.MIN_VALUE)
                .withCategoryTitle(CategoryType.FOOD.title)
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());

        Response<Product> response = productService.putProduct(zeroProduct)
                .execute();

        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Product with id: -9223372036854775808 doesn't exist");
        }
        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product MAX Id (Negative Test)")
    @Test
    void putProductMaxNegativeTest() throws IOException {
        Product zeroProduct = new Product()
                .withId((int) Long.MAX_VALUE)
                .withCategoryTitle(CategoryType.FOOD.title)
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());

        Response<Product> response = productService.putProduct(zeroProduct)
                .execute();

        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Product with id: 9223372036854775807 doesn't exist");
        }
        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product Change Price Test")
    @Test
    void putProductChangePriceTest() throws IOException {
        Product banana = new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(TestDataSet.PRODUCT_BANANAMAMA_TITLE);

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Products dbProduct = productsMapper.selectByPrimaryKey(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID));
        Categories dbCategory = categoriesMapper.selectByPrimaryKey(dbProduct.getCategory_id().intValue());


        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.isSuccessful()).isTrue();
        assertThat(changedProduct.getId()).isEqualTo(dbProduct.getId());
        assertThat(changedProduct.getTitle()).isEqualTo(dbProduct.getTitle());
        assertThat(changedProduct.getCategoryTitle()).isEqualTo(dbCategory.getTitle());
        assertThat(changedProduct.getPrice()).isEqualTo(dbProduct.getPrice());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }
    ///Цена = 0
    @SneakyThrows
    @DisplayName("Put Product Zero Price (Negative Test)")
    @Test
    void putProductZeroPriceNegativeTest() throws IOException {
        Product banana = new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice(0)
                .withTitle(TestDataSet.PRODUCT_BANANAMAMA_TITLE);

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(400);
        assertThat(response.isSuccessful()).isTrue();
        assertThat(changedProduct.getPrice()).isEqualTo(banana.getPrice());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    /// Цена = Null
    @SneakyThrows
    @DisplayName("Put Product Null Price (Negative Test)")
    @Test
    void putProductNullPriceNegativeTest() throws IOException {
        Product banana = new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice((Integer) null)
                .withTitle(TestDataSet.PRODUCT_BANANAMAMA_TITLE);

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(400);
        assertThat(response.isSuccessful()).isTrue();
        assertThat(changedProduct.getPrice()).isEqualTo(banana.getPrice());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product MAX Price (Negative Test)")
    @Test
    void putProductMAXPriceNegativeTest() throws IOException {
        Product banana = new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice(Integer.MAX_VALUE)
                .withTitle(TestDataSet.PRODUCT_BANANAMAMA_TITLE);

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.isSuccessful()).isTrue();
        assertThat(changedProduct.getPrice()).isEqualTo(banana.getPrice());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }
    @SneakyThrows
    @DisplayName("Put Product MIN Price (Negative Test)")
    @Test
    void putProductMINPriceNegativeTest() throws IOException {
        Product banana = new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice(Integer.MIN_VALUE)
                .withTitle(TestDataSet.PRODUCT_BANANAMAMA_TITLE);

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(400);
        assertThat(response.isSuccessful()).isTrue();
        assertThat(changedProduct.getPrice()).isEqualTo(banana.getPrice());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product Text Price (Negative Test)")
    @Test
    void putProductTextPriceNegativeTest() throws IOException {
        Product banana = (Product) new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice("Один");

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(400);
        assertThat(response.isSuccessful()).isFalse();

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product Fractional Price Test")
    @Test
    void putProductFractionalPriceTest() throws IOException {
        Product banana = new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice((int) 77.7)
                .withTitle(TestDataSet.PRODUCT_BANANAMAMA_TITLE);

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.isSuccessful()).isTrue();
        assertThat(changedProduct.getPrice()).isEqualTo(banana.getPrice());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @SneakyThrows
    @DisplayName("Put Product Huge Title (Negative Test)")
    @Test
    void putProductHugeTitleNegativeTest() throws IOException {
        Product banana = new Product()
                .withId(Math.toIntExact(TestDataSet.PRODUCT_BANANAMAMA_ID))
                .withCategoryTitle(TestDataSet.PRODUCT_BANANAMAMA_TYPE.title)
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(TestDataSet.PRODUCT_BANANAMAMA_TITLE_Big);

        Response<Product> response = productService.putProduct(banana)
                .execute();

        Product changedProduct = response.body();
        assertThat(response.code()).isEqualTo(400);
        assertThat(response.isSuccessful()).isFalse();

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }
    ///Удаление продукта
    @SneakyThrows
    @DisplayName("Delete Product (Positive Test)")
    @Test
    void deleteProductPositiveTest() throws IOException {
        //product.setId(0);
        Response<Product> response =
                (Response<Product>) productService.createProduct(product)
                        .execute();
        productId = response.body().getId();

        /// Удаление созданного продукта

        Response<ResponseBody> deleteResponse =
                productService.deleteProduct(productId)
                        .execute();
        assertThat(deleteResponse.code()).isEqualTo(200);
        assertThat(deleteResponse.isSuccessful()).isTrue();

        ///Проверяем, что продукт удален
        Products dbProduct = productsMapper.selectByPrimaryKey(productId);
        assertThat(dbProduct).isEqualTo(null);

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());

//        Response<Product> getResponse = productService.getProduct(productId)
//                .execute();
//
//        assertThat(getResponse.code()).isEqualTo(404);
//        if (getResponse != null && !getResponse.isSuccessful() && getResponse.errorBody() != null) {
//            ResponseBody body = getResponse.errorBody();
//            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
//            ErrorBody errorBody = converter.convert(body);
//            assertThat(errorBody.getMessage()).isEqualTo("Unable to find product with id: " + productId);
//        }
        productId = null;
    }

    @AfterEach
    void tearDown() {
        if (productId != null) {
            try {
                retrofit2.Response<ResponseBody> response =
                        productService.deleteProduct(productId)
                                .execute();
                assertThat(response.isSuccessful()).isTrue();
            } catch (IOException e) {

            }
        }

    }
}