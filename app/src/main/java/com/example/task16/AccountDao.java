package com.example.task16;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface AccountDao {

    @Insert
    void addAccount(Account account);


    @Query("SELECT * FROM account_table WHERE login = :login")
    Account getAccountByLogin(String login);


    @Query("SELECT * FROM account_table")
    List<Account> getListAccount();

    @Query("SELECT count(*)!=0 FROM account_table WHERE login = :login ")
    boolean containsPrimaryKey(String login);

    @Query("UPDATE account_table SET level=:level WHERE login = :login")
    void update(String login, int level);

}
