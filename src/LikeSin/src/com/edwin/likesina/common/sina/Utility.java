package com.edwin.likesina.common.sina;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.utils.MD5;
import com.sina.weibo.sdk.utils.NetworkHelper;

public class Utility
{
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final byte[] decodes = new byte[256];

    public static Bundle parseUrl(String url)
    {
        try
        {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        }
        catch (MalformedURLException e)
        {
        }
        return new Bundle();
    }

    public static void showToast(String content, Context ct)
    {
        Toast.makeText(ct, content, 1).show();
    }

    public static Bundle decodeUrl(String s)
    {
        Bundle params = new Bundle();
        if (s != null)
        {
            String[] array = s.split("&");
            for (String parameter : array)
            {
                String[] v = parameter.split("=");
                try
                {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"), URLDecoder.decode(v[1], "UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return params;
    }

    public static String packUrl(HashMap<String, String> params)
    {
        if (params == null) { return ""; }

        StringBuilder sb = new StringBuilder();
        String value = "";
        boolean bFirst = true;
        for (String key : params.keySet())
        {
            value = (String)params.get(key);
            if ((TextUtils.isEmpty(key)) || (TextUtils.isEmpty(value))) continue;
            try
            {
                if (bFirst) bFirst = false;
                else
                {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

        }

        return sb.toString();
    }

    public static Bundle unpackUrl(String url)
    {
        Bundle params = new Bundle();
        if (url != null)
        {
            String[] array = url.split("&");
            for (String parameter : array)
            {
                String[] v = parameter.split("=");
                try
                {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"), URLDecoder.decode(v[1], "UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return params;
    }

    public static String encodeUrl(WeiboParameters parameters)
    {
        if (parameters == null) { return ""; }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int loc = 0; loc < parameters.size(); loc++)
        {
            if (first) first = false;
            else
            {
                sb.append("&");
            }
            String _key = parameters.getKey(loc);
            String _value = parameters.getValue(_key);
            if (_value == null) Log.i("encodeUrl", "key:" + _key + " 's value is null");
            else
            {
                try
                {
                    sb.append(URLEncoder.encode(parameters.getKey(loc), "UTF-8") + "="
                            + URLEncoder.encode(parameters.getValue(loc), "UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
            Log.i("encodeUrl", sb.toString());
        }
        return sb.toString();
    }

    public static String encodeParameters(WeiboParameters httpParams)
    {
        if ((httpParams == null) || (isBundleEmpty(httpParams))) { return ""; }
        StringBuilder buf = new StringBuilder();
        int j = 0;
        for (int loc = 0; loc < httpParams.size(); loc++)
        {
            String key = httpParams.getKey(loc);
            if (j != 0) buf.append("&");
            try
            {
                buf.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException)
            {
            }
            j++;
        }
        return buf.toString();
    }

    public static Bundle formBundle(Oauth2AccessToken oat)
    {
        Bundle params = new Bundle();
        params.putString("access_token", oat.getToken());
        params.putString("refresh_token", oat.getRefreshToken());
        params.putString("expires_in", oat.getExpiresTime() + "");
        return params;
    }

    public static Bundle formErrorBundle(Exception e)
    {
        Bundle params = new Bundle();
        params.putString("error", e.getMessage());
        return params;
    }

    private static boolean isBundleEmpty(WeiboParameters bundle)
    {
        return (bundle == null) || (bundle.size() == 0);
    }

    public static String encodeBase62(byte[] data)
    {
        StringBuffer sb = new StringBuffer(data.length * 2);
        int pos = 0;
        int val = 0;
        for (int i = 0; i < data.length; i++)
        {
            val = val << 8 | data[i] & 0xFF;
            pos += 8;
            while (pos > 5)
            {
                pos -= 6;
                char c = encodes[(val >> pos)];
                sb.append(c == '/' ? "ic" : c == '+' ? "ib" : c == 'i' ? "ia" : Character.valueOf(c));
                val &= (1 << pos) - 1;
            }
        }
        if (pos > 0)
        {
            char c = encodes[(val << 6 - pos)];
            sb.append(c == '/' ? "ic" : c == '+' ? "ib" : c == 'i' ? "ia" : Character.valueOf(c));
        }
        return sb.toString();
    }

    public static byte[] decodeBase62(String string)
    {
        if (string == null) { return null; }
        char[] data = string.toCharArray();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(string.toCharArray().length);
        int pos = 0;
        int val = 0;
        for (int i = 0; i < data.length; i++)
        {
            char c = data[i];
            if (c == 'i')
            {
                i++;
                c = data[i];

                i--;

                c = c == 'c' ? '/' : c == 'b' ? '+' : c == 'a' ? 'i' : data[i];
            }
            val = val << 6 | decodes[c];
            pos += 6;
            while (pos > 7)
            {
                pos -= 8;
                baos.write(val >> pos);
                val &= (1 << pos) - 1;
            }
        }
        return baos.toByteArray();
    }

    private static boolean deleteDependon(File file, int maxRetryCount)
    {
        int retryCount = 1;
        maxRetryCount = maxRetryCount < 1 ? 5 : maxRetryCount;
        boolean isDeleted = false;

        if (file != null) do
        {
            if (((isDeleted = file.delete())))
            {
                continue;
            }
            retryCount++;
        }
        while ((!isDeleted) && (retryCount <= maxRetryCount) && (file.isFile()) && (file.exists()));

        return isDeleted;
    }

    private static void mkdirs(File dir_)
    {
        if (dir_ == null) { return; }
        if ((!dir_.exists()) && (!dir_.mkdirs())) throw new RuntimeException("fail to make " + dir_.getAbsolutePath());
    }

    private static void createNewFile(File file_)
    {
        if (file_ == null) { return; }
        if (!__createNewFile(file_)) throw new RuntimeException(file_.getAbsolutePath() + " doesn't be created!");
    }

    private static void delete(File f)
    {
        if ((f != null) && (f.exists()) && (!f.delete())) throw new RuntimeException(f.getAbsolutePath() + " doesn't be deleted!");
    }

    private static boolean __createNewFile(File file_)
    {
        if (file_ == null) { return false; }
        makesureParentExist(file_);
        if (file_.exists()) delete(file_);
        try
        {
            return file_.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean deleteDependon(String filepath, int maxRetryCount)
    {
        if (TextUtils.isEmpty(filepath)) return false;
        return deleteDependon(new File(filepath), maxRetryCount);
    }

    private static boolean deleteDependon(String filepath)
    {
        return deleteDependon(filepath, 0);
    }

    private static boolean doesExisted(File file)
    {
        return (file != null) && (file.exists());
    }

    private static boolean doesExisted(String filepath)
    {
        if (TextUtils.isEmpty(filepath)) return false;
        return doesExisted(new File(filepath));
    }

    private static void makesureParentExist(File file_)
    {
        if (file_ == null) { return; }
        File parent = file_.getParentFile();
        if ((parent != null) && (!parent.exists())) mkdirs(parent);
    }

    private static void makesureFileExist(File file)
    {
        if (file == null) return;
        if (!file.exists())
        {
            makesureParentExist(file);
            createNewFile(file);
        }
    }

    private static void makesureFileExist(String filePath_)
    {
        if (filePath_ == null) return;
        makesureFileExist(new File(filePath_));
    }

    public static boolean isWifi(Context mContext)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService("connectivity");
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetInfo != null) && (activeNetInfo.getType() == 1);
    }

    public static String generateGUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean isChineseLocale(Context context)
    {
        try
        {
            Locale locale = context.getResources().getConfiguration().locale;
            if ((Locale.CHINA.equals(locale)) || (Locale.CHINESE.equals(locale)) || (Locale.SIMPLIFIED_CHINESE.equals(locale))
                    || (Locale.TAIWAN.equals(locale))) return true;
        }
        catch (Exception e)
        {
            return true;
        }
        return false;
    }

    public static Bundle errorSAX(String responsetext)
    {
        Bundle mErrorBun = new Bundle();
        if ((responsetext != null) && (responsetext.indexOf("{") >= 0))
        {
            try
            {
                JSONObject json = new JSONObject(responsetext);
                mErrorBun.putString("error", json.optString("error"));
                mErrorBun.putString("error_code", json.optString("error_code"));
                mErrorBun.putString("error_description", json.optString("error_description"));
            }
            catch (JSONException e)
            {
                mErrorBun.putString("error", "JSONExceptionerror");
            }
        }

        return mErrorBun;
    }

    public static String getSign(Context context, String pkgName)
    {
        PackageInfo packageInfo;
        try
        {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 64);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
            return null;
        }
        for (int j = 0; j < packageInfo.signatures.length; j++)
        {
            byte[] str = packageInfo.signatures[j].toByteArray();

            if (str != null) { return MD5.hexdigest(str); }
        }
        return null;
    }

    public static final class UploadImageUtils
    {
        private static void revitionImageSizeHD(String picfile, int size, int quality) throws IOException
        {
            if (size <= 0) { throw new IllegalArgumentException("size must be greater than 0!"); }
            if (new File(picfile).isFile()) { throw new FileNotFoundException(picfile == null ? "null" : picfile); }

            if (!BitmapHelper.verifyBitmap(picfile)) { throw new IOException(""); }

            int photoSizesOrg = 2 * size;
            FileInputStream input = new FileInputStream(picfile);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opts);
            try
            {
                input.close();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }

            int rate = 0;
            for (int i = 0;; i++)
            {
                if ((opts.outWidth >> i <= photoSizesOrg) && (opts.outHeight >> i <= photoSizesOrg))
                {
                    rate = i;
                    break;
                }
            }

            opts.inSampleSize = (int)Math.pow(2.0D, rate);
            opts.inJustDecodeBounds = false;

            Bitmap temp = safeDecodeBimtapFile(picfile, opts);

            if (temp == null) { throw new IOException("Bitmap decode error!"); }

            // Utility.access$1(picfile);
            // Utility.access$2(picfile);

            int org = temp.getWidth() > temp.getHeight() ? temp.getWidth() : temp.getHeight();
            float rateOutPut = size / org;

            Bitmap outputBitmap = null;
            if (rateOutPut < 1.0F)
            {
                // while (true)
                try
                {
                    outputBitmap = Bitmap.createBitmap((int)(temp.getWidth() * rateOutPut), (int)(temp.getHeight() * rateOutPut),
                            Bitmap.Config.ARGB_8888);
                }
                catch (OutOfMemoryError e)
                {
                    // Bitmap outputBitmap;
                    System.gc();
                    rateOutPut = (float)(rateOutPut * 0.8D);
                }
                // Bitmap outputBitmap;
                if (outputBitmap == null)
                {
                    temp.recycle();
                }
                Canvas canvas = new Canvas(outputBitmap);
                Matrix matrix = new Matrix();
                matrix.setScale(rateOutPut, rateOutPut);
                canvas.drawBitmap(temp, matrix, new Paint());
                temp.recycle();
                temp = outputBitmap;
            }
            FileOutputStream output = new FileOutputStream(picfile);
            if ((opts != null) && (opts.outMimeType != null) && (opts.outMimeType.contains("png"))) temp.compress(
                    Bitmap.CompressFormat.PNG, quality, output);
            else temp.compress(Bitmap.CompressFormat.JPEG, quality, output);
            try
            {
                output.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            temp.recycle();
        }

        private static void revitionImageSize(String picfile, int size, int quality) throws IOException
        {
            if (size <= 0) { throw new IllegalArgumentException("size must be greater than 0!"); }

            if (new File(picfile).exists()) { throw new FileNotFoundException(picfile == null ? "null" : picfile); }

            if (!BitmapHelper.verifyBitmap(picfile)) { throw new IOException(""); }

            FileInputStream input = new FileInputStream(picfile);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opts);
            try
            {
                input.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            int rate = 0;
            for (int i = 0;; i++)
            {
                if ((opts.outWidth >> i <= size) && (opts.outHeight >> i <= size))
                {
                    rate = i;
                    break;
                }
            }

            opts.inSampleSize = (int)Math.pow(2.0D, rate);
            opts.inJustDecodeBounds = false;

            Bitmap temp = safeDecodeBimtapFile(picfile, opts);

            if (temp == null) { throw new IOException("Bitmap decode error!"); }

            // Utility.access$1(picfile);
            // Utility.access$2(picfile);
            FileOutputStream output = new FileOutputStream(picfile);
            if ((opts != null) && (opts.outMimeType != null) && (opts.outMimeType.contains("png"))) temp.compress(
                    Bitmap.CompressFormat.PNG, quality, output);
            else temp.compress(Bitmap.CompressFormat.JPEG, quality, output);
            try
            {
                output.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            temp.recycle();
        }

        public static boolean revitionPostImageSize(Context context, String picfile)
        {
            try
            {
                if (NetworkHelper.isWifiValid(context)) revitionImageSizeHD(picfile, 1600, 75);
                else
                {
                    revitionImageSize(picfile, 1024, 75);
                }

                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return false;
        }

        private static Bitmap safeDecodeBimtapFile(String bmpFile, BitmapFactory.Options opts)
        {
            BitmapFactory.Options optsTmp = opts;
            if (optsTmp == null)
            {
                optsTmp = new BitmapFactory.Options();
                optsTmp.inSampleSize = 1;
            }

            Bitmap bmp = null;
            FileInputStream input = null;

            int MAX_TRIAL = 5;
            for (int i = 0; i < 5; i++)
            {
                try
                {
                    input = new FileInputStream(bmpFile);
                    bmp = BitmapFactory.decodeStream(input, null, opts);
                    try
                    {
                        input.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                catch (OutOfMemoryError e)
                {
                    e.printStackTrace();
                    optsTmp.inSampleSize *= 2;
                    try
                    {
                        input.close();
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                catch (FileNotFoundException e)
                {
                }
            }
            return bmp;
        }
    }
}