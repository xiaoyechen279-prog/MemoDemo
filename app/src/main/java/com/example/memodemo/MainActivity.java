package com.example.memodemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvMemo;
    private MemoAdapter adapter;
    private MemoDao memoDao;
    private EditText etSearch;
    private ImageView btnClear;
    private TextView tvCount;
    private View llEmpty;
    private TextView tvEmpty;
    private TextView tvEmptyHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 内容延伸到状态栏 / 导航栏下方，由页面自行避让（沉浸式）
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        lvMemo = findViewById(R.id.lv_memo);
        etSearch = findViewById(R.id.et_search);
        btnClear = findViewById(R.id.btn_clear);
        tvCount = findViewById(R.id.tv_count);
        llEmpty = findViewById(R.id.ll_empty);
        tvEmpty = findViewById(R.id.tv_empty);
        tvEmptyHint = findViewById(R.id.tv_empty_hint);
        ExtendedFloatingActionButton btnAdd = findViewById(R.id.btn_add);

        setupSystemBars();
        applyWindowInsets();

        memoDao = new MemoDao(this);
        adapter = new MemoAdapter(this, new ArrayList<>());
        lvMemo.setAdapter(adapter);

        // 搜索框文本变化监听，实时过滤
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
                refreshList();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 清空搜索
        btnClear.setOnClickListener(v -> etSearch.setText(""));

        // 新建备忘录
        btnAdd.setOnClickListener(v -> showMemoDialog(null));

        // 条目点击 / 长按：列表项卡片自带涟漪与点击态，故由 Adapter 回调
        adapter.setOnMemoActionListener(new MemoAdapter.OnMemoActionListener() {
            @Override
            public void onClick(Memo memo) {
                showMemoDialog(memo);
            }

            @Override
            public void onLongClick(Memo memo) {
                showDeleteDialog(memo);
            }
        });

        refreshList();
    }

    /** 状态栏图标固定白色（头部为深色渐变），导航栏图标跟随深浅色模式 */
    private void setupSystemBars() {
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNight = uiMode == Configuration.UI_MODE_NIGHT_YES;
        controller.setAppearanceLightNavigationBars(!isNight);
    }

    /** 让头部、列表、悬浮按钮各自避让状态栏与导航栏 */
    private void applyWindowInsets() {
        final View root = findViewById(R.id.root);
        final View header = findViewById(R.id.header);
        final ListView list = findViewById(R.id.lv_memo);
        final View fab = findViewById(R.id.btn_add);

        final int headerTop = header.getPaddingTop();
        final int listBottom = list.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // 头部内容下移一个状态栏高度，渐变背景依然铺满状态栏区域
            header.setPadding(header.getPaddingLeft(), headerTop + bars.top,
                    header.getPaddingRight(), header.getPaddingBottom());
            // 列表底部让出导航栏
            list.setPadding(list.getPaddingLeft(), list.getPaddingTop(),
                    list.getPaddingRight(), listBottom + bars.bottom);
            // 悬浮按钮避开导航栏
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            lp.bottomMargin = dp(24) + bars.bottom;
            fab.setLayoutParams(lp);

            return windowInsets;
        });
    }

    /** 弹出新增 / 编辑备忘录弹窗 */
    private void showMemoDialog(Memo editMemo) {
        View view = getLayoutInflater().inflate(R.layout.dialog_memo, null);
        TextView tvDialogTitle = view.findViewById(R.id.tv_dialog_title);
        TextInputLayout tilTitle = view.findViewById(R.id.til_title);
        EditText etTitle = view.findViewById(R.id.et_title);
        EditText etContent = view.findViewById(R.id.et_content);
        MaterialCheckBox cbImportant = view.findViewById(R.id.cb_important);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        final boolean isEdit = editMemo != null;
        tvDialogTitle.setText(isEdit ? R.string.dialog_title_edit : R.string.dialog_title_new);

        // 编辑时填充原有数据
        if (isEdit) {
            etTitle.setText(editMemo.getTitle());
            etContent.setText(editMemo.getContent());
            cbImportant.setChecked(editMemo.isImportant());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(view)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText() == null ? "" : etTitle.getText().toString().trim();
            String content = etContent.getText() == null ? "" : etContent.getText().toString().trim();

            if (title.isEmpty()) {
                tilTitle.setError(getString(R.string.error_title_empty));
                etTitle.requestFocus();
                return;
            }
            tilTitle.setError(null);

            boolean isImportant = cbImportant.isChecked();
            if (isEdit) {
                memoDao.updateMemo(editMemo.getId(), title, content, isImportant);
            } else {
                memoDao.addMemo(title, content, isImportant);
            }
            refreshList();
            dialog.dismiss();
        });

        dialog.show();
    }

    /** 删除确认弹窗 */
    private void showDeleteDialog(final Memo memo) {
        String title = memo.getTitle() == null || memo.getTitle().trim().isEmpty()
                ? getString(R.string.untitled) : memo.getTitle();

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_dialog_title)
                .setMessage(getString(R.string.delete_dialog_message, title))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (d, which) -> {
                    memoDao.deleteMemo(memo.getId());
                    refreshList();
                })
                .show();

        // 删除属于破坏性操作，按钮单独标红
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setTextColor(ContextCompat.getColor(this, R.color.danger));
        }
    }

    /** 刷新列表、计数与空状态 */
    private void refreshList() {
        String keyword = etSearch.getText().toString().trim();
        List<Memo> list = memoDao.queryMemoByKey(keyword);
        adapter.setData(list);
        updateEmptyState(list.size(), keyword.isEmpty());
    }

    private void updateEmptyState(int count, boolean noKeyword) {
        tvCount.setText(getString(R.string.home_count, count));

        boolean empty = count == 0;
        llEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (empty) {
            tvEmpty.setText(noKeyword ? R.string.empty_title : R.string.empty_search_title);
            tvEmptyHint.setText(noKeyword ? R.string.empty_hint : R.string.empty_search_hint);
        }
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }
}
