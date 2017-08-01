package me.appstockus.poosh_android.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.FileEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Created by CITILINK-PC on 29.10.16.
 */
public class HttpHelper
{
    public static final String BASE_URL = "http://178.62.41.116/api/v1/";

    //addresses:
    public static final String pathUsers = "users";
    public static final String pathVerify = "verify";
    public static final String pathContacts = "contacts";
    public static final String pathFriends = "friends";
    public static final String pathSounds = "sounds";
    public static final String pathPooshes = "pooshes";
    public static final String pathUpload = "upload";


    //RequestMethods:
    public enum RequestMethod {GET,  POST,  PUT,  DELETE}


    //Status codes:
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_ERROR_VALIDATION = 422;
    public static final int CODE_ERROR_AUTH = 403;

    private static final AsyncHttpClient client = new AsyncHttpClient();
   // HttpClient client = new DefaultHttpClient();


    /******************************************
     *                  Send
     *****************************************/

    public static void sendRequest(final RequestBuilder builder) {
        final String errorMessage = verify(builder);

        if(errorMessage.length() > 0) {
            Log.d("HttpHelper fail", errorMessage);

          //  if(builder.responseHandler != null)
           //     builder.responseHandler.onErrorResponse( builder.url, -1, errorMessage );
        }
        else {
            final AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {;
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    builder.responseHandler.onSuccessResponse(
                            responseBody,
                            statusCode,
                            (String) getTag()
                    );
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    builder.responseHandler.onErrorResponse(
                            responseBody,
                            statusCode,
                            (String) getTag()
                    );
                }
            };
            handler.setTag( builder.tag != null ? builder.tag : builder.url);

            client.removeAllHeaders();
            final String token=DataManager.instance.userModel.token;
            if(token.length() > 0){
                client.addHeader("Authorization", "Bearer " + token);}


            switch (builder.requestMethod) {
                case GET:
                    //todo: get file
                    client.get(
                            builder.context,
                            builder.url,
                            new StringEntity(builder.requestParams, builder.contentType),
                            builder.contentType.getMimeType(),
                            handler
                    );
                    break;

                case POST:
                    String boundary=builder.generateBoundary();
                    if(builder.contentType== ContentType.APPLICATION_JSON){
                        client.addHeader("Accept", "application/json");
                        client.post(
                                builder.context,
                                builder.url,
                                new StringEntity(builder.requestParams, builder.contentType),
                                builder.contentType.getMimeType(),
                                handler

                        );
                    }else{

                        client.addHeader(client.HEADER_CONTENT_TYPE,  "multipart/form-data; boundary=" +boundary);

                        builder.contentType=ContentType.MULTIPART_FORM_DATA;
                        ContentType contentType= ContentType.create("audio/mpeg","ISO_8859_1");

                        client.post(
                                builder.context,
                                builder.url,
                                MultipartEntityBuilder.create()
                                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                                        .addBinaryBody("file", builder.requestFile,contentType,builder.requestFile.getName())
                                        .setBoundary(boundary)
                                        .build(),
                                "multipart/form-data",
                                handler
                        );
                    }
                    break;

                case PUT:
                    client.post(
                            builder.context,
                            builder.url,
                            new StringEntity(builder.requestParams, builder.contentType),
                            builder.contentType.getMimeType(),
                            handler
                    );
                    break;

                case DELETE:
                    client.delete(
                            builder.context,
                            builder.url,
                            new StringEntity(builder.requestParams, builder.contentType),
                            builder.contentType.getMimeType(),
                            handler
                    );
                    break;
            }
        }
    }

    private static String verify(RequestBuilder builder) {
        final StringBuilder errorMessage = new StringBuilder();

        if(builder == null) {
            errorMessage.append("Builder is null");
        }
        else {
            if(builder.responseHandler == null)
                errorMessage.append("ResponseHandler is null\n");

            if(builder.url == null || builder.url.length() <= 0)
                errorMessage.append("URL is null or empty\n");

            if(builder.requestMethod == null)
                errorMessage.append("Request method is null\n");
        }

        return  errorMessage.toString();
    }


    /******************************************
     *             RequestBuilder
     *****************************************/

    public static final class RequestBuilder
    {
        private Context context;
        private String url = BASE_URL;
        private RequestMethod requestMethod;
        private ContentType contentType = ContentType.TEXT_PLAIN;

//        private RequestParams requestParams;
        private String requestParams = "";
        private File requestFile;
        private BasicClientCookie cookie;
        private ResponseHandler responseHandler;
        private String tag;
        private final static char[] MULTIPART_CHARS =
                "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        .toCharArray();

        public RequestBuilder(Context context) {
            this.context = context;
        }

        public RequestBuilder(Context context, String baseUrl) {
            this.context = context;
            this.url = baseUrl;
        }

        public RequestBuilder addPath(String []path) {
            final StringBuilder builder = new StringBuilder(this.url);

            if( builder.charAt( builder.length() - 1 ) != '/' )
                builder.append('/');

            for(String p : path) {
                builder.append(p).append('/');
            }

            this.url = builder.toString();

            return this;
        }

        public RequestBuilder setFile(@NonNull String audioPath) throws FileNotFoundException {
            this.contentType = ContentType.MULTIPART_FORM_DATA;
           this.requestFile = new File(audioPath);

           // this.requestFile  = new RequestParams();
           // this.requestFile.put("file", new File(audioPath));
            this.contentType = ContentType.MULTIPART_FORM_DATA;
         //   this.contentType.getMimeType();
            return this;
        }

        public RequestBuilder setMethod(RequestMethod method) {
            this.requestMethod = method;
            return this;
        }

        public RequestBuilder setParams(JSONObject params) {
            this.requestParams = params.toString();
            this.contentType = ContentType.APPLICATION_JSON;
            return this;
        }

        public RequestBuilder setCookies(BasicClientCookie cookie) {
            this.cookie = cookie;
            return this;
        }

        public RequestBuilder setHandler(ResponseHandler handler) {
            this.responseHandler = handler;
            return this;
        }

        public RequestBuilder setTag(String tag) {
            this.tag = tag;
            return this;
        }
        private String generateBoundary() {
            final StringBuilder buffer = new StringBuilder();
            final Random rand = new Random();
            final int count = rand.nextInt(11) + 30; // a random size from 30 to 40
            for (int i = 0; i < count; i++) {
                buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
            }
            return buffer.toString();
        }
    }

    /******************************************
     *             ResponseHandler
     *****************************************/

    public interface ResponseHandler
    {
        void onSuccessResponse(byte []data, int code, String tag);

        void onErrorResponse(byte []data, int code, String tag);
    }
}
