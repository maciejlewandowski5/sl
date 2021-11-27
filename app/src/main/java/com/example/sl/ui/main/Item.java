package com.example.sl.ui.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.sl.R;
import com.example.sl.databinding.ActivityItemBinding;
import com.example.sl.databinding.ItemRecyclerViewBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Item extends AppCompatActivity {
    private ItemsViewModel itemsViewModel;
    private static String SHOP_ID = "section_number";
    private ActivityItemBinding binding;
    private static String IS_ARCHIVED = "is_archived";
    private String[] shopId = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final String shopIdTmp = getIntent().getStringExtra(SHOP_ID);
        Integer page = getIntent().getIntExtra(IS_ARCHIVED, 2);
        shopId[0] = shopIdTmp;
        String collection;
        if (page != 2) {
            collection = "shops";
        } else {
            collection = "archive";
        }
        itemsViewModel =
                new ViewModelProvider(this, new ItemsListViewModelFactory(
                        this,
                        collection
                ))
                        .get(ItemsViewModel.class);

        itemsViewModel.setShopId(shopIdTmp);
        itemsViewModel.setPage(page);

        itemsViewModel.getPage().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 2) {
                    binding.floatingActionButton.setVisibility(INVISIBLE);
                    binding.getRoot().removeView(binding.linearLayout);
                } else {
                    binding.floatingActionButton.setVisibility(VISIBLE);
                }
            }
        });

        itemsViewModel.getShopId().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                shopId[0] = s;
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(mLayoutManager);

        ItemAdpater flowersAdapter = new ItemAdpater(new Function1<ItemElement, Unit>() {
            @Override
            public Unit invoke(ItemElement ie) {
                if (itemsViewModel.getPage().getValue() != 2) {
                    itemsViewModel.removeItem(shopId[0], ie);
                }
                return null;
            }
        });
        binding.recycler.setAdapter(flowersAdapter);

        itemsViewModel.getFlowersLiveData().observe(this, new Observer<State<List<ItemElement>>>() {
            @Override
            public void onChanged(State<List<ItemElement>> state) {
                if (state.isSuccess()) {
                    if (Objects.requireNonNull(state.getValue()).isEmpty()) {
                        binding.newItem.setVisibility(VISIBLE);
                    } else {
                        binding.newItem.setVisibility(INVISIBLE);
                    }
                    binding.progressBar2.setVisibility(INVISIBLE);
                    flowersAdapter.submitList(state.getValue());
                } else if (state.isError()) {
                    binding.newItem.setVisibility(VISIBLE);
                    binding.newItem.setText(state.getError());
                } else if (state.isLoading()) {
                    binding.newItem.setVisibility(INVISIBLE);
                    binding.progressBar2.setVisibility(VISIBLE);
                }

            }
        });

        itemsViewModel.fetchItems(shopId[0]);


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

        AppCompatActivity that = this;
        itemsViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    Toast.makeText(that, s, Toast.LENGTH_SHORT).show();
                    itemsViewModel.clearError();
                }
            }
        });

        itemsViewModel.getActionsInBackground().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    binding.progressBar3.setProgress(50);
                } else if (aBoolean != null) {
                    binding.progressBar3.setProgress(100);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                        }
                    }, 500);
                }
            }
        });

    }

    private void hideProgress() {
        binding.progressBar3.setProgress(0);
    }

    public void addItem(View view) {
        if (binding.input.getText().toString().isEmpty()) {
            binding.errorText.setVisibility(VISIBLE);
        } else {
            binding.errorText.setVisibility(INVISIBLE);
            itemsViewModel.addItem(shopId[0], binding.input.getText().toString(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            binding.input.setText("");
        }
    }
}