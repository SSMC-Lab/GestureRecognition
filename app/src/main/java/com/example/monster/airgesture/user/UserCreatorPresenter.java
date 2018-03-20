package com.example.monster.airgesture.user;

import com.example.monster.airgesture.data.DataProvider;
import com.example.monster.airgesture.data.IDataSource;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.base.BasePresenter;
import com.example.monster.airgesture.utils.AlphabetUtils;
import com.example.monster.airgesture.utils.LogUtils;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.List;

/**
 * Presenter实现类 {@link IUserCreatorContract.Presenter}
 * Created by Welkinshadow on 2018/1/21.
 */

public class UserCreatorPresenter extends BasePresenter<IUserCreatorContract.View>
        implements IUserCreatorContract.Presenter<IUserCreatorContract.View> {

    private IDataSource dataRepository;
    private List<Integer> letterMapping;

    public UserCreatorPresenter() {
        dataRepository = DataProvider.provideDataRepository();
        letterMapping = dataRepository.getDefaultUser().getLetterMapping();//初始值为默认构造序列
    }

    @Override
    public void saveLetterMapping(List<String> selectedLetter, int code) {
        for (String letter : selectedLetter) {
            if (!StringUtils.isEmpty(letter) && letter.length() <= 1) {
                int position = AlphabetUtils.INSTANCE.getPosition(letter.charAt(0));
                letterMapping.set(position, code);
            } else {
                LogUtils.e("letter length > 1");
                getView().createFailed("字符长度大于1");
            }
        }
    }

    @Override
    public void createUser(String userName) {
        User user = new User(userName, letterMapping);
        dataRepository.createDictionaryByHabit(user, new IDataSource.OnUserCreateListener() {
            @Override
            public void OnStart() {
                getView().showLoading();
            }

            @Override
            public void OnSuccessful() {
                getView().hideLoading();
                getView().createSuccessful();
            }

            @Override
            public void OnFailed() {
                getView().hideLoading();
                getView().createFailed("未知原因导致的错误");
            }
        });
    }
}
