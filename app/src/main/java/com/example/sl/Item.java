package com.example.sl;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.sl.Keys.DEFAULT_ARCHIVE_PAGE;
import static com.example.sl.Keys.IS_ARCHIVED;
import static com.example.sl.Keys.SHOP_ID;
import static com.example.sl.UtilsKt.resolveCollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.sl.ItemsListViewModelFactory;
import com.example.sl.ItemsViewModel;
import com.example.sl.databinding.ActivityItemBinding;
import com.example.sl.ui.main.adapters.ItemAdapter;
import com.example.sl.model.ItemElement;
import com.example.sl.model.State;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Item extends AppCompatActivity {
    private ItemsViewModel itemsViewModel;
    private ActivityItemBinding binding;
    private final String[] shopId = new String[1];

    private static final int PROGRESS_HALF = 50;
    private static final int PROGRESS_FULL = 100;
    private static final int PROGRESS_RESET_TIME_MILLISECONDS = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shopId[0] = getIntent().getStringExtra(SHOP_ID);
        int page = getIntent().getIntExtra(IS_ARCHIVED, DEFAULT_ARCHIVE_PAGE);
        String collection = resolveCollection(page);

        initializeItemsViewModel(collection, page);

        itemsViewModel.getPage().observe(this, this::configureTab);
        itemsViewModel.getShopId().observe(this, s -> shopId[0] = s);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(mLayoutManager);

        ItemAdapter itemsAdapter = initializeItemAdapter();
        binding.recycler.setAdapter(itemsAdapter);

        itemsViewModel.getItems().observe(
                this,
                state -> updateUiOnItemsElementsChange(state, itemsAdapter));
        itemsViewModel.fetchItems(shopId[0]);

        clearInputWhenTextChanged();
        toastOnError();
        notifyUiAboutActionsInBackground();
    }

    public void notifyUiAboutActionsInBackground() {
        itemsViewModel.getActionsInBackground().observe(this, isRunning -> {
            if (isRunning != null && isRunning) {
                binding.progressBar3.setProgress(PROGRESS_HALF);
            } else if (isRunning != null) {
                binding.progressBar3.setProgress(PROGRESS_FULL);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(this::hideProgress, PROGRESS_RESET_TIME_MILLISECONDS);
            }
        });
    }

    public void toastOnError() {
        AppCompatActivity that = this;
        itemsViewModel.getErrorMessage().observe(this, s -> {
            if (s != null) {
                Toast.makeText(that, s, Toast.LENGTH_SHORT).show();
                itemsViewModel.clearError();
            }
        });
    }

    public void clearInputWhenTextChanged() {
        binding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.errorText.setVisibility(INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void updateUiOnItemsElementsChange(State<List<ItemElement>> state, ItemAdapter adapter) {
        if (state.isSuccess()) {
            resolveEmptyOrNot(state);
            adapter.submitList(state.getValue());
        } else if (state.isError()) {
            binding.newItem.setVisibility(VISIBLE);
            binding.newItem.setText(state.getError());
        } else if (state.isLoading()) {
            binding.newItem.setVisibility(INVISIBLE);
            binding.progressBar2.setVisibility(VISIBLE);
        }
    }

    public void resolveEmptyOrNot(State<List<ItemElement>> state) {
        if (Objects.requireNonNull(state.getValue()).isEmpty()) {
            binding.newItem.setVisibility(VISIBLE);
        } else {
            binding.newItem.setVisibility(INVISIBLE);
        }
        binding.progressBar2.setVisibility(INVISIBLE);
    }

    private void initializeItemsViewModel(String collection, int page) {
        itemsViewModel = new ViewModelProvider(this, new ItemsListViewModelFactory(collection))
                .get(ItemsViewModel.class);
        itemsViewModel.setShopId(shopId[0]);
        itemsViewModel.setPage(page);
    }

    private ItemAdapter initializeItemAdapter() {
        return new ItemAdapter(itemElement -> {
            Integer value = itemsViewModel.getPage().getValue();
            if (value != null) {
                if (value != 2) {
                    itemsViewModel.removeItem(shopId[0], itemElement);
                }
            }
            return null;
        });
    }

    private void configureTab(int tabNumber) {
        if (tabNumber == DEFAULT_ARCHIVE_PAGE) {
            binding.floatingActionButton.setVisibility(INVISIBLE);
            binding.getRoot().removeView(binding.linearLayout);
        } else {
            binding.floatingActionButton.setVisibility(VISIBLE);
        }
    }

    private void hideProgress() {
        binding.progressBar3.setProgress(0);
    }

    public void addItem(View view) {
        if (Objects.requireNonNull(binding.input.getText()).toString().isEmpty()) {
            binding.errorText.setVisibility(VISIBLE);
        } else {
            binding.errorText.setVisibility(INVISIBLE);
            itemsViewModel.addItem(shopId[0], binding.input.getText().toString(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            binding.input.setText("");
        }
    }
}