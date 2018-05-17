package com.briskemen.upfiledemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.nex3z.flowlayout.FlowLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout     ll_add;//添加图片
    private Button           bt_commit;//提交
    private FlowLayout       fl_content;
    private int              dim_160;
    /**
     * 选择图片集合
     */
    private List<LocalMedia> selectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initDate();
    }

    public void initView() {
        ll_add = findViewById(R.id.ll_add);
        bt_commit = findViewById(R.id.bt_commit);
        fl_content = findViewById(R.id.fl_content);
    }

    public void initListener() {
        bt_commit.setOnClickListener(this);
        ll_add.setOnClickListener(this);
    }

    public void initDate() {
        selectList = new ArrayList<>();
        dim_160 = getResources().getDimensionPixelSize(R.dimen.d_160);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_commit:
                upFile();
                break;
            case R.id.ll_add:
                goSelectPic();
                break;

            default:
                break;
        }
    }

    /**
     * 上传文件
     */
    private void upFile() {
        int size  = selectList == null ? 0 : selectList.size();
        if (size >= 1){
            List<File> files = new ArrayList();
            for (int i = 0; i < selectList.size(); i++) {
                String pathname = selectList.get(i).getCompressPath();
                File file = new File(pathname);
                files.add(file);
            }
            //这里是后台地址
            String filesUrl = "";
            uploadMultiFiles(filesUrl,files);
        }else {
            Toast.makeText(this,"请选择图片再上传",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 单图上传
     */
    private void uploadMultiFile(String url, File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                //设置超时
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
       /* okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String jsonStr = response.body().string();
                    Log.i("EvaluateActivity", "uploadMultiFile() response=" + jsonStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    /**
     * 上传多图片
     */
    private void uploadMultiFiles(String url, List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            builder.addFormDataPart("file", file.getName(), fileBody);
        }

        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();

        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                //设置超时
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try {
                    String jsonStr = response.body().string();
                    Log.i("EvaluateActivity", "uploadMultiFile() response=" + jsonStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }


    /**
     * 选择图片
     */
    private void goSelectPic() {
        selectList.clear();
        fl_content.removeAllViews();
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()
                // 、视频.ofVideo()、音频.ofAudio()
              //  .theme(R.style.picture_white_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R
                // .style.picture.white.style
                .maxSelectNum(9)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or
                // PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                //                        .withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //                        .hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
                //                        .isGif()// 是否显示gif图片 true or false
                .compressSavePath(getPath())//压缩图片保存地址
                //                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                //                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                //                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true
                // or false
                //                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true
                // or false
                //                        .openClickSound(false)// 是否开启点击声音 true or false
                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                //                        .cropCompressQuality()// 裁剪压缩质量 默认90 int
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //                        .cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                //                        .rotateEnabled() // 裁剪是否可旋转图片 true or false
                //                        .scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                //                        .videoQuality()// 视频录制质量 0 or 1 int
                //                        .videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                //                        .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                //                        .recordVideoSecond()//视频秒数录制 默认60s int
                //                        .isDragFrame(true)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    //选择图片后之前的上传的就要清除，重新上传
                    // 图片选择结果回调
                    selectList.clear();
                    selectList.addAll(PictureSelector.obtainMultipleResult(data));

                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    for (int i = 0; i < selectList.size(); i++) {
                        LocalMedia media = selectList.get(i);
                        Log.i("图片-----》", media.getPath());
                        ImageView imageView = new ImageView(this);
                        fl_content.addView(imageView);
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.width = dim_160;
                        layoutParams.height = dim_160;
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.color.main_sel)
                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                        Glide.with(this)
                                .load(media.getCompressPath())
                                .apply(options)
                                .into(imageView);
                    }
                    break;
            }
        }
    }

    /**
     * 自定义压缩存储地址
     *
     * @return
     */
    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/passPayShop/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

}
