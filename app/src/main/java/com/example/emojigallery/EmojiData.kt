package com.example.emojigallery

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. 定义一张表：emojis
// 包含 id(自动生成), imageUrl(图片网址), title(标题)
@Entity(tableName = "emojis")
data class EmojiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String,
    val title: String
)

// 2. 定义操作工具 (DAO)
@Dao
interface EmojiDao {
    // 获取所有数据，返回 Flow (数据流)，这样数据库一变，界面自动刷新
    @Query("SELECT * FROM emojis")
    fun getAllEmojis(): Flow<List<EmojiEntity>>

    // 插入数据，如果有重复就替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(emojis: List<EmojiEntity>)

    // 查询总共有多少条数据
    @Query("SELECT COUNT(*) FROM emojis")
    suspend fun getCount(): Int
}

// 3. 定义数据库核心
@Database(entities = [EmojiEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao

    // 单例模式：确保整个 APP 只有一个数据库连接，节省内存
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "emoji_db" // 数据库文件的名字
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
