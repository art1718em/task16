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

import com.example.task16.databinding.FragmentEntranceBinding;


public class EntranceFragment extends Fragment {

    FragmentEntranceBinding binding;


    MutableLiveData<Account> liveDataAcc = new MutableLiveData<>();


    Observer<Account> obs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEntranceBinding.inflate(inflater, container, false);
        AccountDB accountDB = AccountDB.getInstance(requireContext());
        AccountDao accountDao =accountDB.accountDao();


        obs = account -> {
            if(liveDataAcc.getValue() == null){
                Toast.makeText(getContext(), "Такого логина нет, зарегистрируйтесь!", Toast.LENGTH_SHORT).show();
            }
            else{
                if(liveDataAcc.getValue().getPassword().equals(binding.password.getText().toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("login", liveDataAcc.getValue().getLogin()+" "+liveDataAcc.getValue().getLevel());
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_entranceFragment_to_gameFragment, bundle);
                }
                else
                    Toast.makeText(getContext(), "Неверный пароль", Toast.LENGTH_SHORT).show();
            }
        };

        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = binding.login.getText().toString();
                String password = binding.password.getText().toString();
                if(!login.equals("") && !password.equals("")){
                    new Thread(() -> {
                        if(accountDao.containsPrimaryKey(login)){
                            liveDataAcc.postValue(accountDao.getAccountByLogin(login));
                        }
                        else
                            liveDataAcc.postValue(null);
                    }).start();
                }
            }
        });

        binding.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_entranceFragment_to_registrationFragment);
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