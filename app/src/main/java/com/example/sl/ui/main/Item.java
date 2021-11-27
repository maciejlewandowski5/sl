package com.example.sl.ui.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.sl.R;
import com.example.sl.databinding.ActivityItemBinding;
import com.example.sl.databinding.ItemRecyclerViewBinding;

import java.time.LocalDateTime;
import java.util.List;
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

        itemsViewModel =
                new ViewModelProvider(this, new ItemsListViewModelFactory(this))
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

        itemsViewModel.getFlowersLiveData().observe(this, new Observer<List<ItemElement>>() {
            @Override
            public void onChanged(List<ItemElement> shops) {
                if (shops.isEmpty()) {
                    binding.newItem.setVisibility(VISIBLE);
                } else {
                    binding.newItem.setVisibility(INVISIBLE);
                    binding.progressBar2.setVisibility(INVISIBLE);
                }
                flowersAdapter.submitList(shops);
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

    }

    public void addItem(View view) {
        if (binding.input.getText().toString().isEmpty()) {
            binding.errorText.setVisibility(VISIBLE);
        } else {
            binding.errorText.setVisibility(INVISIBLE);
            itemsViewModel.addItem(shopId[0], binding.input.getText().toString(), "2021-15-03 08:30");
            binding.input.setText("");
        }
    }
}