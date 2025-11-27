package com.example.emojigallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmojiViewModel(application: Application) : AndroidViewModel(application) {

    // 获取数据库工具
    private val dao = AppDatabase.getDatabase(application).emojiDao()

    // === 【核心逻辑】 ===
    // 将数据库的数据转换成界面可观察的状态 (StateFlow)
    // 界面只需要盯着这个 emojiList 看就行了
    val emojiList = dao.getAllEmojis()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // ViewModel 启动时，自动执行初始化
        initializeData()
    }

    private fun initializeData() {
        // 在后台线程 (IO) 执行，不卡顿主界面
        viewModelScope.launch(Dispatchers.IO) {
            // 如果数据库是空的，说明是第一次安装，我们需要生成数据
            if (dao.getCount() == 0) {
                val list = mutableListOf<EmojiEntity>()

                // 循环生成 1000 条数据
                for (i in 1..1000) {
                    // 这里使用 robohash 网站生成随机猫咪图片
                    // 格式：https://robohash.org/任意字符?set=set4
                    list.add(
                        EmojiEntity(
                            imageUrl = "https://robohash.org/$i?set=set4",
                            title = "猫咪表情包 #$i"
                        )
                    )
                }

                // 一次性存入数据库
                dao.insertAll(list)
            }
        }
    }
}
