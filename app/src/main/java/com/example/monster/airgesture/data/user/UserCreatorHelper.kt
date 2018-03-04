package com.example.monster.airgesture.data.user


import com.example.monster.airgesture.data.bean.CandidateWord
import com.example.monster.airgesture.data.bean.User
import com.example.monster.airgesture.data.database.DatabaseManager
import com.example.monster.airgesture.utils.AlphabetUtils
import com.example.monster.airgesture.utils.LogUtils

/**
 * 数据库创建辅助
 * Created by Welkinshadow on 2018/1/20.
 */

internal class UserCreatorHelper {

    companion object {
        fun createNewDictionary(user: User, repository: UserRepository, listener: IUserDataSource.OnCreateListener) {
            val dictionaryDB = DatabaseManager.getInstance().getDatabase(DatabaseManager.DB_NAME_DICTIONARY)
            val tableName = user.dictionaryName

            dictionaryDB.execSQL(
                    "CREATE TABLE IF NOT EXISTS $tableName (" +
                            "word TEXT(120), " +
                            "probability DOUBLE, " +
                            "length INTEGER," +
                            "code TEXT(120))")

            val defaultTableName = repository.defaultUser.name
            val cursor = dictionaryDB.rawQuery("SELECT * FROM $defaultTableName", null)

            var word: String
            var length: Int
            var probability: Double
            val newCode = StringBuilder()

            var j = 0
            LogUtils.d("get data over")
            if (cursor.moveToFirst()) {
                do {
                    probability = cursor.getDouble(cursor.getColumnIndex(CandidateWord.PROBABILITY))
                    length = cursor.getInt(cursor.getColumnIndex(CandidateWord.LENGTH))
                    word = cursor.getString(cursor.getColumnIndex(CandidateWord.WORD))
                    //生成编码序列
                    for (i in 0 until word.length) {
                        newCode.append(user.letterMapping[AlphabetUtils.getPosition(word[i])])
                    }
                    if (j++ < 10) {//输出前10个编码结果
                        LogUtils.d("$word : $newCode")
                    }
                    dictionaryDB.execSQL("INSERT INTO $tableName (word,probability,length,code)" +
                            "VALUES ('$word', $probability, $length, '$newCode')")
                    newCode.delete(0, newCode.length)
                } while (cursor.moveToNext())
                listener.OnSuccessful()
                cursor.close()
            } else {
                listener.OnFailed()
            }
        }
    }
}
