package zensar.android.assignments.location.networking;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zensar.android.assignments.location.utility.Constants;

/**
 * Created by RK51670 on 05-02-2018.
 */
public class RetrofitAdapter {

    static OkHttpClient client=null;

    private RetrofitAdapter(){

    }
    public static RetrofitService create(){
        HttpLoggingInterceptor interceptor =new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client=new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retorfitAdapter = new Retrofit.Builder().baseUrl(Constants.GOOGLE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retorfitAdapter.create(RetrofitService.class);
    }

    public static void cancelRetrofitRequest(){
        if(client!=null){
            client.dispatcher().cancelAll();
        }
    }
}