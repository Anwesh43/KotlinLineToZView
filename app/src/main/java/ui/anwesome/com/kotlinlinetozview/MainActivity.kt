package ui.anwesome.com.kotlinlinetozview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.linetozview.LineToZView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LineToZView.create(this)
    }
}
