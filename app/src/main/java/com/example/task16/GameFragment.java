package com.example.task16;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.task16.databinding.FragmentGameBinding;

import java.util.Random;

public class GameFragment extends Fragment {

    FragmentGameBinding binding;

    MutableLiveData<Account> liveDataAcc = new MutableLiveData<>();


    Observer<Account> obs;

    private int count;

    private int attempt = 3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);

        String[] str = ((String)getArguments().getSerializable("login")).split(" ");
        binding.login.setText("Логин: " + str[0]);
        binding.level.setText("Уровень: " + str[1]);
        binding.attempt.setText("Попыток: " + attempt);
        AccountDB accountDB = AccountDB.getInstance(requireContext());
        AccountDao accountDao =accountDB.accountDao();

        count = (int)(Math.random()*20);

        new Thread(() -> {
            liveDataAcc.postValue(accountDao.getAccountByLogin(str[0]));
        }).start();

        obs = account -> {
            new Thread(() -> {
                accountDao.update(liveDataAcc.getValue().getLogin(), liveDataAcc.getValue().getLevel());
            }).start();
        };


        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.et.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Вы не ввели число", Toast.LENGTH_SHORT).show();
                else{
                    if(Integer.parseInt(binding.et.getText().toString()) > count){
                        Toast.makeText(getContext(), "Загаданное число меньше", Toast.LENGTH_SHORT).show();
                        attempt--;
                        binding.attempt.setText("Попыток: " + attempt);
                    }
                    else if(Integer.parseInt(binding.et.getText().toString()) < count){
                        Toast.makeText(getContext(), "Загаданное число больше", Toast.LENGTH_SHORT).show();
                        attempt--;
                        binding.attempt.setText("Попыток: " + attempt);
                    }
                    else{
                        Toast.makeText(getContext(), "Вы угадали и переходите на новый уровень",
                                Toast.LENGTH_SHORT).show();
                        attempt = 3;
                        binding.attempt.setText("Попыток: " + attempt);
                        count = (int)(Math.random()*20);
                        Account account = liveDataAcc.getValue();
                        account.setLevel(account.getLevel()+1);
                        liveDataAcc.setValue(account);
                        binding.level.setText("Уровень: " + liveDataAcc.getValue().getLevel());
                    }
                    if(attempt < 1){
                        Toast.makeText(getContext(), "Вы не угадали, загадано другое число",
                                Toast.LENGTH_SHORT).show();
                        attempt = 3;
                        binding.attempt.setText("Попыток: " + attempt);
                        count = (int)(Math.random()*20);
                    }
                }

            }
        });








        return binding.getRoot();
    }



    @Override
    public void onStart() {
        super.onStart();
        liveDataAcc.observe(this, obs);
    }

    @Override
    public void onStop() {
        super.onStop();
        liveDataAcc.removeObserver(obs);
    }
}