package com.example.task16;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.task16.databinding.FragmentRegistrationBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationFragment extends Fragment {

    FragmentRegistrationBinding binding;

    private RegistrationViewModel vm;

    MutableLiveData<Boolean> liveData = new MutableLiveData<Boolean>();
    Observer<Boolean> observer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

         vm = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);

        AccountDB accountDB = AccountDB.getInstance(requireContext());
        AccountDao accountDao =accountDB.accountDao();



        observer = s -> {
            if(liveData.getValue()) {
                Toast.makeText(getContext(), "Такой логин уже есть в базе", Toast.LENGTH_SHORT).show();
            }
            else{
                new Thread(() -> {
                    Account account = new Account(binding.login.getText().toString(),
                            binding.password.getText().toString(), 1);
                    accountDao.addAccount(account);
                }).start();
                Bundle bundle = new Bundle();
                bundle.putString("login", binding.login.getText().toString()+" 1");
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_registrationFragment_to_gameFragment, bundle);
            }
        };



        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginAndPassword()){
                    new Thread(() -> {
                        liveData.postValue(accountDao.containsPrimaryKey(binding.login.getText().toString()));
                    }).start();
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        liveData.observe(this, observer);
    }

    @Override
    public void onStop() {
        super.onStop();
        liveData.removeObserver(observer);
    }

    private boolean checkLoginAndPassword(){
        String login = binding.login.getText().toString();
        String password = binding.password.getText().toString();
        String passwordAgain = binding.passwordAgain.getText().toString();
        if(login.equals("") || passwordAgain.equals("") || password.equals("")){
            Toast.makeText(getContext(), "Вы ввели не все поля", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(passwordAgain)){
            Toast.makeText(getContext(), "Ваши пароли не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!isPassword(password)){
            Toast.makeText(getContext(), "Ваш пароль не является надежным", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }
    private boolean isPassword(String str){
        Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[A-z])(?=.*[a-z])[\\S]{8,}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}