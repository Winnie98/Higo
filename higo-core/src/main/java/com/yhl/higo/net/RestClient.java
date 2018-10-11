package com.yhl.higo.net;

import android.content.Context;
import android.util.Log;

import com.yhl.higo.net.callback.IError;
import com.yhl.higo.net.callback.IFailure;
import com.yhl.higo.net.callback.IRequest;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.net.callback.RequestCallBacks;
import com.yhl.higo.net.download.DownloadHandler;
import com.yhl.higo.ui.loader.HigoLoader;
import com.yhl.higo.ui.loader.LoaderStyle;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class RestClient {

    private final String URL;
    //    private final Map<String,Object> PARAMS;
    private static final WeakHashMap<String,Object> PARAMS = RestCreator.getParams();
    private final IRequest REQUEST;//回调
    private final String DOWNLOAD_DIR;//下载的目录
    private final String EXTENSION;//下载的文件的后缀
    private final String NAME;//下载的文件名
    private final ISuccess SUCCESS;//成功
    private final IFailure FAILURE;//出错
    private final IError ERROR;//失败
    private final RequestBody BODY;
    private final LoaderStyle LOADER_STYLE;
    private final File FILE;
    private  final Context CONTEXT;

    public RestClient(String url,
                      Map<String, Object> params,
                      String downloadDir,
                      String extension,
                      String name,
                      IRequest request,
                      ISuccess success,
                      IFailure failure,
                      IError error,
                      RequestBody body,
                      File file,
                      Context context,
                      LoaderStyle loaderStyle) {
        this.URL = url;
        //this.PARAMS = PARAMS;
        PARAMS.putAll(params);
        this.DOWNLOAD_DIR  = downloadDir;
        this.EXTENSION = extension;
        this.NAME = name;
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.BODY = body;
        this.FILE = file;
        this.CONTEXT = context;
        this.LOADER_STYLE = loaderStyle;
    }

    public static RestClientBuilder builder(){
//        Log.w("Builder builder", "success");
        return new RestClientBuilder();
    }

    private void request(HttpMethod method){
        final RestService service = RestCreator.getRestService();
        Call<String> call =null;

        if (REQUEST!=null){
            REQUEST.onRequestStart();
        }

        if (LOADER_STYLE!=null){
            HigoLoader.showLoading(CONTEXT,LOADER_STYLE);
        }

        switch (method){
            case GET:
                call = service.get(URL,PARAMS);
                break;
            case POST:
                call = service.post(URL,PARAMS);
                break;
            case POST_RAW:
                call = service.postRaw(URL,BODY);
                break;
            case PUT:
                call = service.put(URL,PARAMS);
                break;
            case PUT_RAW:
                call = service.putRaw(URL,BODY);
                break;
            case DELETE:
                call = service.delete(URL,PARAMS);
                break;
            case UPLOAD:
                final RequestBody requestBody =
                        RequestBody.create(MediaType.parse(MultipartBody.FORM.toString()),FILE);
                final MultipartBody.Part body =
                        MultipartBody.Part.createFormData("upload_file",FILE.getName(),requestBody);
//                call = RestCreator.getRestService().upload(URL,body);
                call = service.upload(URL,body);
                break;
            default:
                break;
        }
        if (call!=null){
            call.enqueue(getRequestCallback());
        }

    }

    private Callback<String> getRequestCallback(){
        return new RequestCallBacks(
                REQUEST,
                SUCCESS,
                FAILURE,
                ERROR,
                LOADER_STYLE
        );
    }

    public final void get(){
        request(HttpMethod.GET);
    }

    public final void post(){
        if (BODY == null) {
            request(HttpMethod.POST);
        }else{
            if (!PARAMS.isEmpty()){
                throw new RuntimeException("params must be null！");
            }
            request(HttpMethod.POST_RAW);
        }
//        Log.w("RestClient post", "success");
    }

    public final void put(){
        if (BODY == null) {
            request(HttpMethod.PUT);
        }else{
            if (!PARAMS.isEmpty()){
                throw new RuntimeException("params must be null！");
            }
            request(HttpMethod.PUT_RAW);
        }
    }

    public final void delete(){
        request(HttpMethod.DELETE);
    }

    public final void upload(){
        request(HttpMethod.UPLOAD);
    }

    public final void download(){
        new DownloadHandler(URL,REQUEST,DOWNLOAD_DIR,EXTENSION,NAME,SUCCESS,FAILURE,ERROR).handleDownload();
    }

}

