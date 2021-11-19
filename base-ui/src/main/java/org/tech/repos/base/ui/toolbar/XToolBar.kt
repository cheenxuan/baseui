package org.tech.repos.base.ui.toolbar

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.tech.repos.base.lib.utils.DisplayUtil
import org.tech.repos.base.ui.R


/**
 * Author: xuan
 * Created on 2021/7/10 12:25.
 *
 * Describe:
 */
open class XToolBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var titleView: TextView? = null
    private var titleImageView: ImageView? = null
    private var subTitleView: TextView? = null
    private var subTitleImageView: ImageView? = null
    private var actionBack: ImageView? = null
    private var radioGroup: RadioGroup? = null

    init {

//        setPadding(0,DisplayUtil.dp2px(25f,resources),0,0)

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.XToolBar)

        //解析action_back
        val actionBackEnable = typeArray.getBoolean(R.styleable.XToolBar_actionEnable, true)
        val actionBackDrawable = typeArray.getDrawable(R.styleable.XToolBar_action_back)
        parseActionBack(actionBackEnable, actionBackDrawable)

        //解析title资源属性
        val titleType = typeArray.getInteger(R.styleable.XToolBar_titleType, 0)
        val titleStr = typeArray.getString(R.styleable.XToolBar_title)
        val titleImage = typeArray.getDrawable(R.styleable.XToolBar_titleIamge)
        val titleResourseId = typeArray.getResourceId(R.styleable.XToolBar_titleAppearance, 0)
        parseTitle(titleType, titleResourseId, titleStr, titleImage)

        //解析subtitle资源属性
        val subTitleEnable = typeArray.getBoolean(R.styleable.XToolBar_subTitleEnable, false)
        val subTitleType = typeArray.getInteger(R.styleable.XToolBar_subTitleType, 0)
        val subTitleStr = typeArray.getString(R.styleable.XToolBar_subTitle)
        val subTitleImage = typeArray.getDrawable(R.styleable.XToolBar_subTitleIamge)
        val subTitleResourseId = typeArray.getResourceId(R.styleable.XToolBar_subTitleAppearance, 0)
        parseSubTitle(subTitleEnable, subTitleType, subTitleResourseId, subTitleStr, subTitleImage)

        val showLine = typeArray.getBoolean(R.styleable.XToolBar_showLine, false)
        val showLineColor = typeArray.getColor(R.styleable.XToolBar_toolbarBotoomLineColor, Color.parseColor("#d3d3d3"))
        if(showLine){
            val params = LayoutParams(LayoutParams.MATCH_PARENT, 2)
            params.addRule(ALIGN_PARENT_BOTTOM)
            val line = View(context)
            line.setBackgroundColor(showLineColor)
            addView(line,params)
        }

        typeArray.recycle()
        
        
    }

    private fun parseSubTitle(
        subTitleEnable: Boolean,
        subTitleType: Int,
        subTitleResourseId: Int,
        subTitle: String?,
        subTitleImage: Drawable?
    ) {
        if (subTitleEnable) {
            val array =
                context.obtainStyledAttributes(subTitleResourseId, R.styleable.titleAppearance)

            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            params.addRule(ALIGN_PARENT_END)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.marginEnd = DisplayUtil.dp2px(16f, resources)
            } else {
                params.rightMargin = DisplayUtil.dp2px(16f, resources)
            }

            if (subTitleType == 0) {//文字模式
                val subTitleSize = array.getDimensionPixelSize(
                    R.styleable.titleAppearance_textSize,
                    applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
                )
                val subTitleColor =
                    array.getColor(R.styleable.titleAppearance_textColor, Color.BLACK)
                
                subTitleView =
                    createSubTitleView(subTitleColor, TypedValue.COMPLEX_UNIT_PX, subTitleSize)
                subTitleView?.setText(subTitle)

                addView(subTitleView, params)
            } else if (subTitleType == 1) {//图片模式
                if (subTitleImage != null) {
                    subTitleImageView = ImageView(context)
                    subTitleImageView?.setImageDrawable(subTitleImage)
                    addView(subTitleImageView, params)
                }
            }

            array.recycle()
        }
    }

    private fun parseTitle(
        titleType: Int,
        titleResourseId: Int,
        title: String?,
        titleImage: Drawable?
    ) {
        val params = LayoutParams(DisplayUtil.dp2px(250f, resources), LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_IN_PARENT)


        if (titleType == 0) {//文字模式
            val array = context.obtainStyledAttributes(titleResourseId, R.styleable.titleAppearance)
            var titleSize = 0
            if(!TextUtils.isEmpty(title)){
                when {
                    title?.length ?: 0 < 12 -> {
                        titleSize = array.getDimensionPixelSize(
                            R.styleable.titleAppearance_textSize,
                            applyUnit(TypedValue.COMPLEX_UNIT_SP, 18f)
                        )
                    }
                    title?.length in 12..16 -> {
                        titleSize = array.getDimensionPixelSize(
                            R.styleable.titleAppearance_textSize,
                            applyUnit(TypedValue.COMPLEX_UNIT_SP, 17f)
                        )
                    }
                    else -> {
                        titleSize = array.getDimensionPixelSize(
                            R.styleable.titleAppearance_textSize,
                            applyUnit(TypedValue.COMPLEX_UNIT_SP, 16f)
                        )
                    }
                }
                
            }
            val titleColor = array.getColor(R.styleable.titleAppearance_textColor, Color.BLACK)
            array.recycle()

            //创建Title
            titleView = TextView(context)
            titleView?.text = if (TextUtils.isEmpty(title)) "" else title
            titleView?.setLines(1)
            titleView?.gravity = Gravity.CENTER
            titleView?.ellipsize = TextUtils.TruncateAt.END
            titleView?.setTextColor(titleColor)
            titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat())

            addView(titleView, params)

        } else if (titleType == 1) {//图片模式
            if (titleImage != null) {
                titleImageView = ImageView(context)
                titleImageView?.setImageDrawable(titleImage)
                addView(titleImageView, params)
            }
        }
    }

    private fun parseActionBack(actionBackEnable: Boolean, actionBackDrawable: Drawable?) {
        if (actionBackEnable) { //开启返回按钮
            var drawable: Drawable? = actionBackDrawable
            if (drawable == null) drawable = ContextCompat.getDrawable(
                context,
                R.drawable.ic_left_arrow
            )

            actionBack = ImageView(context)
            actionBack?.setImageDrawable(drawable)

            val width = DisplayUtil.dp2px(25f, resources)
            val params = LayoutParams(width, width)
            val margin = DisplayUtil.dp2px(14f, resources)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(ALIGN_PARENT_START)
                params.marginStart = margin
            } else {
                params.addRule(ALIGN_PARENT_LEFT)
                params.leftMargin = margin
            }
            params.addRule(CENTER_VERTICAL)


            addView(actionBack, params)
            actionBack?.visibility = View.VISIBLE

            actionBack?.setOnClickListener {
                if (context is Activity) {
                    (context as Activity).onBackPressed()
                }
            }
        } else {
            actionBack?.visibility = View.GONE
        }
    }

    fun changeSubTitleText(show: Boolean, subTitle: String? = null) {

        if (!show) {
            if (subTitleView != null && subTitleView?.visibility == View.VISIBLE) {
                subTitleView?.visibility = View.GONE
            }
        } else {
            if (subTitleImageView != null && subTitleImageView?.visibility == View.VISIBLE) {
                subTitleImageView?.visibility = View.GONE
            }

            if (subTitleView == null) {
                subTitleView = createSubTitleView()

                val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                params.addRule(ALIGN_PARENT_END)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.marginEnd = DisplayUtil.dp2px(16f, resources)
                } else {
                    params.rightMargin = DisplayUtil.dp2px(16f, resources)
                }
                addView(subTitleImageView, params)
            }

            if (subTitleView?.visibility == View.VISIBLE && subTitle == subTitleView?.text) {
                return
            }

            subTitleView?.setText(subTitle)
            subTitleView?.visibility = View.VISIBLE
        }
    }

    private fun createSubTitleView(
        textColor: Int = resources.getColor(R.color.color_666),
        textTypeUnit: Int = TypedValue.COMPLEX_UNIT_SP,
        textSize: Int = 15
    ): TextView {
        val titleView = TextView(context)
        titleView.setTextColor(textColor)
        titleView.setTextSize(textTypeUnit, textSize.toFloat())
        titleView?.setLines(1)
        titleView?.gravity = Gravity.CENTER
        titleView?.ellipsize = TextUtils.TruncateAt.END

        return titleView
    }

    fun changeTitle(title: String?) {
        titleView?.let {
            if (TextUtils.isEmpty(title)) {
                titleView?.text = ""
            } else {
                val titleSize  = when {
                        title?.length ?: 0 < 12 -> {
                            applyUnit(TypedValue.COMPLEX_UNIT_SP, 18f)
                        }
                        title?.length in 12..16 -> {
                            applyUnit(TypedValue.COMPLEX_UNIT_SP, 17f)
                        }
                        else -> {
                            applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
                        }
                    }
                
                titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat())
                titleView?.text = title
            }
        }
    }

    fun showActionBack(show: Boolean) {
        actionBack?.let {
            actionBack!!.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    fun getActionBackEnable(): Boolean {
        return actionBack?.visibility == View.VISIBLE
    }

    fun setOnActionSubTitleCallBack(onPress: (view: View?) -> Unit?) {
        subTitleView?.let { action ->
            action.setOnClickListener {
                onPress(it)
            }
        }
    }

    fun setOnActionBackCallback(onPress: (view: View?) -> Unit?) {
        actionBack?.let { action ->
            action.setOnClickListener {
                onPress(it)
            }
        }
    }

    fun setOnCheckedButtonCallback(checked: (checkedId: Int?) -> Unit?) {
        radioGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                checked.invoke(checkedId)
            }
        })
    }
    
    fun changeSubTitleColor(color:Int){
        subTitleView?.setTextColor(color)
    }

    private fun applyUnit(unit: Int, value: Float): Int {
        return TypedValue.applyDimension(unit, value, resources.displayMetrics).toInt()
    }
}