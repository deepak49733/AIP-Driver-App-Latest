package com.sc.aipdriver.activities.interfaces;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://aip2.nicoleinfosoftdemo.com/";
//    public static final String BASE_URL = "https://studlink.net/";
//    public static final String BASE_URL = "https://studlinktest.nicoleinfosoftdemo.com/";
private static Retrofit retrofitHttp1 = null;
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d("API_LOG", message);
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {

                            SharedPreferences preferences =
                                    context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

                            String token = preferences.getString("token", "");

                            Log.d("TOKEN", token);

                            Request original = chain.request();

                            Request.Builder builder = original.newBuilder();
//                                    .addHeader("Client-Service", "studlink-infosoftcom")
//                                    .addHeader("Auth-Key", "96u9N95MRRv6P75BJK$IxBVRKWpNdr7alHW")
                            // Add token only if available
                            if (token != null && !token.isEmpty()) {
                                builder.header("Authorization", "Bearer " + token);
                            }

                            Request request = builder.build();

                            return chain.proceed(request);
                        }
                    })
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
    public static Retrofit getClient2() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL).client(getHeader2("hh"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static OkHttpClient getHeader2(final String authorizationValue) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("API_LOG", message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = null;
                                if (authorizationValue != null) {
                                    Log.d("--Authorization-- ", authorizationValue);

                                    Request original = chain.request();
                                    // Request customization: add request headers
                                    Request.Builder requestBuilder = original.newBuilder().addHeader("Client-Service", "studlink-infosoftcom")
                                            .addHeader("Auth-Key", "96u9N95MRRv6P75BJK$IxBVRKWpNdr7alHW").addHeader("Content-Type", "application/json");
                                    request = requestBuilder.build();
                                }
                                return chain.proceed(request);
                            }
                        })
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        return okClient;

    }
    public static Retrofit getHttp1Client(Context context) {

        if (retrofitHttp1 == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d("API_LOG", message);
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .protocols(java.util.Collections.singletonList(Protocol.HTTP_1_1))
                    .addInterceptor(interceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {

                            SharedPreferences preferences =
                                    context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

                            String token = preferences.getString("token", "");

                            Request original = chain.request();

                            Request.Builder builder = original.newBuilder();

                            if (token != null && !token.isEmpty()) {
                                builder.header("Authorization", "Bearer " + token);
                            }

                            return chain.proceed(builder.build());
                        }
                    })
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofitHttp1 = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofitHttp1;
    }
}





/*
package com.sc.aipdriver.activities.interfaces;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

*/
/**
 * Created by dev on 21/7/17.
 *//*

public class ApiClient {

    public static final String BASE_URL = "https://aip2.nicoleinfosoftdemo.com/";
//    public static final String BASE_URL = "https://studlink.net/";
//    public static final String BASE_URL = "https://studlinktest.nicoleinfosoftdemo.com/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL).client(getHeader("hh"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClient2() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL).client(getHeader2("hh"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static OkHttpClient getHeader(final String authorizationValue) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                //.addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = null;
                                if (authorizationValue != null) {
                                    Log.d("--Authorization-- ", authorizationValue);
                                    Request original = chain.request();
                                    // Request customization: add request headers
                                    Request.Builder requestBuilder = original.newBuilder().addHeader("Client-Service", "studlink-infosoftcom")
                                            .addHeader("Auth-Key", "96u9N95MRRv6P75BJK$IxBVRKWpNdr7alHW");
                                    request = requestBuilder.build();
                                }
                                return chain.proceed(request);
                            }
                        })
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        return okClient;

    }

    public static OkHttpClient getHeader2(final String authorizationValue) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("API_LOG", message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                //.addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = null;
                                if (authorizationValue != null) {
                                    Log.d("--Authorization-- ", authorizationValue);

                                    Request original = chain.request();
                                    // Request customization: add request headers
                                    Request.Builder requestBuilder = original.newBuilder().addHeader("Client-Service", "studlink-infosoftcom")
                                            .addHeader("Auth-Key", "96u9N95MRRv6P75BJK$IxBVRKWpNdr7alHW").addHeader("Content-Type", "application/json");
                                    request = requestBuilder.build();
                                }
                                return chain.proceed(request);
                            }
                        })
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        return okClient;

    }
}
*/
