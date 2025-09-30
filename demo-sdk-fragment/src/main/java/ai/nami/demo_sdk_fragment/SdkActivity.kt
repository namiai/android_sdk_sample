package ai.nami.demo_sdk_fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SdkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdk)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SdkFragment.newInstance())
                .commitNow()
        }
    }
}