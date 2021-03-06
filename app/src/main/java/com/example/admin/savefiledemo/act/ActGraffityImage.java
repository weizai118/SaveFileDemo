package com.example.admin.savefiledemo.act;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.think.util.StringUtil;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.savefiledemo.Constant;
import com.example.admin.savefiledemo.R;
import com.example.admin.savefiledemo.adapter.GraffityImageAdapter;
import com.example.admin.savefiledemo.util.FileUtil;
import com.example.admin.savefiledemo.util.ImageUtil;
import com.example.admin.savefiledemo.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.forward.androids.utils.ImageUtils;
import cn.hzw.graffiti.GraffitiActivity;
import cn.hzw.graffiti.GraffitiParams;

/**
 * 使用graffiti框架对图片进行涂鸦
 * Created by admin on 2018/1/2.
 */

public class ActGraffityImage extends AppCompatActivity {

    @BindView(R.id.tv_open_graffity)
    TextView tvOpenGraffity;
    @BindView(R.id.list_view)
    ListView listView;//显示结果图

    private List<File> readFilesList = new ArrayList<>();
    private List<File> saveFilesList = new ArrayList<>();
    private GraffityImageAdapter imageListAdapter;

    private static final File READ_FILES_DIR = Constant.getFolderDir(Constant.GRAFFITY_SRC_FILE_PATH);
    private static final File SAVE_FILES_DIR = Constant.getFolderDir(Constant.GRAFFITY_DES_FILE_PATH);
    private static final String signatureImagePath = Constant.getFolderDir(Constant.SIGNAYURE_FILE_PATH).getAbsolutePath()
            +"/" + Constant.SIGNATURE_FILE_NAME;
    public static final int REQUEST_GRAFFITI = 22;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_graffity_image);
        ButterKnife.bind(this);
        if (!READ_FILES_DIR.exists()) {
            READ_FILES_DIR.mkdir();
        }
        if (!SAVE_FILES_DIR.exists()) {
            SAVE_FILES_DIR.mkdir();
        }
        imageListAdapter = new GraffityImageAdapter(ActGraffityImage.this);
        listView.setAdapter(imageListAdapter);

        getReadFilesList();
        getSaveFilesList();
    }

    /**
     * 读取原图
     */
    public void getReadFilesList() {
        if (readFilesList != null) {
            readFilesList.clear();
        }
        readFilesList.addAll(FileUtil.getFiles(READ_FILES_DIR));
        if (readFilesList == null || readFilesList.size() == 0) {
            ToastUtil.showMessage("暂无任何原图");
            return;
        }
    }

    /**
     * 读取结果图
     */
    public void getSaveFilesList(){
        if (saveFilesList != null) {
            saveFilesList.clear();
        }
        saveFilesList.addAll(FileUtil.getFiles(SAVE_FILES_DIR));
        if (saveFilesList == null || saveFilesList.size() == 0) {
            ToastUtil.showMessage("暂无任何结果图");
            return;
        }
        imageListAdapter.initData(0, saveFilesList);
    }

    public void openGraffity() {
        // 涂鸦参数
        GraffitiParams params = new GraffitiParams();
        // 图片路径
        params.mImagePath = READ_FILES_DIR.getAbsolutePath();
        params.mSignPath = signatureImagePath;
        params.mTitleName = "测试图片";
        params.mAmplifierScale = 0;
        params.mSavePath = SAVE_FILES_DIR.getAbsolutePath(); //设置涂鸦后的图片保存的路径
        params.mSavePathIsDir = true;
        params.mPaintSize = 2;//设置初始笔的大小
        params.mIsFullScreen = true; //图片充满全屏
        params.mIsDrawableOutside = false; //不允许涂鸦到图片以外的位置
        ArrayList<String> pathList = new ArrayList<>();
        for (int i = 0; i < readFilesList.size(); i++) {
            pathList.add(readFilesList.get(i).getAbsolutePath());
        }
        GraffitiActivity.startActivityForResult(ActGraffityImage.this, params, pathList, REQUEST_GRAFFITI);
    }

    /**
     * 编辑图片结果回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GRAFFITI) {
            if (data == null) {
                return;
            }
            if (resultCode == GraffitiActivity.RESULT_OK) {
                ArrayList<String> savePaths = data.getStringArrayListExtra(GraffitiActivity.KEY_IMAGE_PATH);
                if (StringUtil.isEmpty(savePaths)) {
                    return;
                }
                getSaveFilesList();//刷新列表数据
            } else if (resultCode == GraffitiActivity.RESULT_ERROR) {
                ToastUtil.showMessage("onActivityResult发生错误");
            }
        }
    }

    @OnClick(R.id.tv_open_graffity)
    public void onViewClicked() {
        if (readFilesList != null && readFilesList.size() > 0) {
            openGraffity();
        }
    }
}
