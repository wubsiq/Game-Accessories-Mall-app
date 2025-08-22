package com.example.dc2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.dc2.table.GameItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;

public class AddEditItemDialog extends Dialog {

    private Context context;
    private GameItem item;
    private BiFunction<String, byte[], Boolean> callback;
    private ImageView ivItemImage;
    private EditText etItemName;
    private byte[] selectedImage;
    public static final int PICK_IMAGE_REQUEST = 1;

    public AddEditItemDialog(Context context, GameItem item,
                             BiFunction<String, byte[], Boolean> callback) {
        super(context);
        this.context = context;
        this.item = item;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_edit_item_dialog);

        // 初始化视图组件
        ivItemImage = findViewById(R.id.ivItemImage);
        etItemName = findViewById(R.id.etItemName);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // 如果是编辑模式，填充现有数据
        if (item != null) {
            etItemName.setText(item.getItem_name());
            if (item.getItem_image() != null) {
                loadImageFromBytes(item.getItem_image());
                selectedImage = item.getItem_image();
            }
        }

        // 选择图片按钮点击事件
        btnSelectImage.setOnClickListener(v -> openImagePicker());

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveItem());

        // 取消按钮点击事件
        btnCancel.setOnClickListener(v -> dismiss());
    }

    /**
     * 打开图片选择器
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((ItemManagementActivity) context).startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * 设置选中的图片
     */
    // 替换 setSelectedImage 中的图片加载
    public void setSelectedImage(Uri imageUri) {
        Glide.with(context)
                .load(imageUri)
                .apply(new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .into(ivItemImage);

        // 异步处理图片转换
        new Thread(() -> {
            try {
                selectedImage = getBytesFromUri(imageUri);
            } catch (IOException e) {
                Log.e("AddEditItemDialog", "Image conversion error", e);
            }
        }).start();
    }

    // 替换 getBytesFromUri 方法，添加图片压缩
    private byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        // 第一步：只获取图片尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        // 第二步：计算缩放比例
        int scale = 1;
        int maxSize = 1024; // 最大尺寸
        if (options.outHeight > maxSize || options.outWidth > maxSize) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(maxSize /
                    (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
        }

        // 第三步：加载缩放后的图片
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();

        // 转换为字节数组
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output); // 80% 质量压缩
        return output.toByteArray();
    }

    /**
     * 保存饰品信息
     */
    private void saveItem() {
        String itemName = etItemName.getText().toString().trim();

        if (itemName.isEmpty()) {
            etItemName.setError("请输入饰品名称");
            etItemName.requestFocus();
            return;
        }

        if (selectedImage == null && item == null) {
            Toast.makeText(context, "请选择饰品图片", Toast.LENGTH_SHORT).show();
            return;
        }

        if (callback.apply(itemName, selectedImage)) {
            dismiss();
        }
    }
    public void setImageBytes(byte[] imageBytes) {
        this.selectedImage = imageBytes;
        loadImageFromBytes(imageBytes);
    }

    private void loadImageFromBytes(byte[] imageBytes) {
        Glide.with(context)
                .load(imageBytes)
                .apply(new RequestOptions().centerCrop())
                .into(ivItemImage);
    }
}