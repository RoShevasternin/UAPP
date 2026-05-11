package com.robuxe.robuxtracker.freerobux.Morefun

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityCharcBinding

class RC_Data_Activity : AppCompatActivity() {

    private lateinit var binding: RcActivityCharcBinding
    private var adapter: RC_Adapter_Accesso? = null
    private val modelList = mutableListOf<RC_Model_DataCommon>()
    private var which: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }


        binding = RcActivityCharcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this@RC_Data_Activity).loadNative(binding.frmNative, "small")
        which = intent.getStringExtra("which")

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        when (which) {
            "char" -> {
                binding.tvtitle.text = "Characters"
                loadCharacters()
                adapter = RC_Adapter_Accesso(this, modelList)
                binding.recyclerView.adapter = adapter
            }
            "emote" -> {
                binding.tvtitle.text = "Emotes Animation"
                loadAnim()
            }
            "bundle" -> {
                binding.tvtitle.text = "Bundle Animation"
                loadAnim()
            }
            "face" -> {
                binding.tvtitle.text = "Face Accessories"
                loadAccessories()
            }
            "head" -> {
                binding.tvtitle.text = "Head Accessories"
                loadAccessories()
            }
            "neck" -> {
                binding.tvtitle.text = "Neck Accessories"
                loadAccessories()
            }
        }
        loadCloth()
        loadHead()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Data_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun loadCharacters() {
        modelList.add(RC_Model_DataCommon("Addison", R.drawable.c1, R.drawable.char0, R.string.ch_addison, 5))
        modelList.add(RC_Model_DataCommon("Blazer Burner", R.drawable.c2, R.drawable.char1, R.string.ch_blazerburner, 2))
        modelList.add(RC_Model_DataCommon("Calico", R.drawable.c3, R.drawable.char2, R.string.ch_calico, 2))
        modelList.add(RC_Model_DataCommon("CapyBara", R.drawable.c4, R.drawable.char3, R.string.ch_capybara, 15))
        modelList.add(RC_Model_DataCommon("Cindy", R.drawable.c5, R.drawable.char4, R.string.ch_cindy, 10))
        modelList.add(RC_Model_DataCommon("City Life Woman", R.drawable.c6, R.drawable.char5, R.string.ch_city_life_woman, 6))
        modelList.add(RC_Model_DataCommon("Claire", R.drawable.c7, R.drawable.char6, R.string.ch_claire, 6))
        modelList.add(RC_Model_DataCommon("Dennis", R.drawable.c8, R.drawable.char7, R.string.ch_dennis, 6))
        modelList.add(RC_Model_DataCommon("Denny", R.drawable.c9, R.drawable.char8, R.string.ch_denny, 8))
        modelList.add(RC_Model_DataCommon("John", R.drawable.c10, R.drawable.char9, R.string.ch_john, 2))
        modelList.add(RC_Model_DataCommon("Junkbot", R.drawable.c11, R.drawable.char10, R.string.ch_junkbot, 3))
        modelList.add(RC_Model_DataCommon("Knights of Redcliff", R.drawable.c12, R.drawable.char11, R.string.ch_knights_of_redcliff, 8))
        modelList.add(RC_Model_DataCommon("Lindsey", R.drawable.c13, R.drawable.char12, R.string.ch_lindsey, 8))
        modelList.add(RC_Model_DataCommon("Linlin", R.drawable.c14, R.drawable.char13, R.string.ch_linlin, 6))
        modelList.add(RC_Model_DataCommon("Monster Hugs", R.drawable.c15, R.drawable.char14, R.string.ch_monster_hugs, 6))
        modelList.add(RC_Model_DataCommon("Oliver", R.drawable.c16, R.drawable.char15, R.string.ch_oliver, 8))
        modelList.add(RC_Model_DataCommon("Peyton", R.drawable.c17, R.drawable.char16, R.string.ch_peyton, 8))
        modelList.add(RC_Model_DataCommon("Roblox Boy", R.drawable.c18, R.drawable.char17, R.string.ch_roblox_boy, 8))
        modelList.add(RC_Model_DataCommon("Squad Ghouls", R.drawable.c19, R.drawable.char18, R.string.ch_squad_ghouls, 6))
        modelList.add(RC_Model_DataCommon("Summer", R.drawable.c20, R.drawable.char19, R.string.ch_summer, 8))
    }

    private fun loadAnim() {
        if ("emote" == which) {
            modelList.add(RC_Model_DataCommon("Ala Yoga Pose", R.drawable.em1, R.drawable.emote_ala_yoga_pose, R.string.emote_ala_yoga_pose, 5))
            modelList.add(RC_Model_DataCommon("Baby Queen", R.drawable.em2, R.drawable.emote_baby_queen, R.string.emote_baby_queen, 5))
            modelList.add(RC_Model_DataCommon("Body Builder", R.drawable.em3, R.drawable.emote_bodybuilder, R.string.emote_bodybuilder, 5))
            modelList.add(RC_Model_DataCommon("Cartwheel", R.drawable.em4, R.drawable.emote_cartwheel, R.string.emote_cartwheel, 5))
            modelList.add(RC_Model_DataCommon("Flex Walk", R.drawable.em5, R.drawable.emote_flex_walk, R.string.emote_flex_walk, 5))
            modelList.add(RC_Model_DataCommon("Floor Rock Freeze", R.drawable.em6, R.drawable.emote_floor_rock_freeze, R.string.emote_floor_rock_freeze, 5))
            modelList.add(RC_Model_DataCommon("Flowing Breeze", R.drawable.em7, R.drawable.emote_flowinh_breeze, R.string.emote_flowinh_breeze, 5))
            modelList.add(RC_Model_DataCommon("Salute", R.drawable.em8, R.drawable.emote_salute, R.string.emote_salute, 5))
            modelList.add(RC_Model_DataCommon("Samba", R.drawable.em9, R.drawable.emote_samba, R.string.emote_samba, 5))
            modelList.add(RC_Model_DataCommon("Tilt", R.drawable.em10, R.drawable.emote_tilt, R.string.emote_tilt, 5))
        } else if ("bundle" == which) {
            modelList.add(RC_Model_DataCommon("Cartoony", R.drawable.an1, R.drawable.bundle_cartoony, R.string.bundle_cartoony, 5))
            modelList.add(RC_Model_DataCommon("Catwalk Glam", R.drawable.an2, R.drawable.bundle_catwalk_glam, R.string.bundle_catwalk_glam, 5))
            modelList.add(RC_Model_DataCommon("Elder", R.drawable.an3, R.drawable.bundle_elder, R.string.bundle_elder, 5))
            modelList.add(RC_Model_DataCommon("Mage", R.drawable.an4, R.drawable.bundle_mage, R.string.bundle_mage, 5))
            modelList.add(RC_Model_DataCommon("NFL", R.drawable.an5, R.drawable.bundle_nfl, R.string.bundle_nfl, 5))
            modelList.add(RC_Model_DataCommon("Rthro", R.drawable.an6, R.drawable.bundle_rthro, R.string.bundle_rthro, 5))
            modelList.add(RC_Model_DataCommon("Stylish", R.drawable.an7, R.drawable.bundle_stylish, R.string.bundle_stylish, 5))
            modelList.add(RC_Model_DataCommon("Superhero", R.drawable.an8, R.drawable.bundle_superhero, R.string.bundle_superhero, 5))
            modelList.add(RC_Model_DataCommon("Toy", R.drawable.an9, R.drawable.bundle_toy, R.string.bundle_toy, 5))
            modelList.add(RC_Model_DataCommon("Vampire", R.drawable.an10, R.drawable.bundle_vampire, R.string.bundle_vampire, 5))
        }
        adapter = RC_Adapter_Accesso(this, modelList)
        binding.recyclerView.adapter = adapter
    }

    private fun loadAccessories() {
        if ("face" == which) {
            modelList.add(RC_Model_DataCommon("Chabbi Cheeks", R.drawable.fc1, R.drawable.face_adorable_chabbi_cheeks, R.string.accessory_face1, 5))
            modelList.add(RC_Model_DataCommon("Blushing", R.drawable.fc2, R.drawable.face_blush_face, R.string.accessory_face2, 5))
            modelList.add(RC_Model_DataCommon("Bored Pretty Look", R.drawable.fc3, R.drawable.face_bored_pretty_face, R.string.accessory_face3, 5))
            modelList.add(RC_Model_DataCommon("Cute Face", R.drawable.fc4, R.drawable.face_cute, R.string.accessory_face4, 5))
            modelList.add(RC_Model_DataCommon("Face with Black", R.drawable.fc5, R.drawable.face_cute_face_with_black_eyes, R.string.accessory_face5, 5))
            modelList.add(RC_Model_DataCommon("Smile Blushing", R.drawable.fc6, R.drawable.face_cute_smile_blushing_face, R.string.accessory_face6, 5))
            modelList.add(RC_Model_DataCommon("Excited Look", R.drawable.fc7, R.drawable.face_excitedcute, R.string.accessory_face7, 5))
            modelList.add(RC_Model_DataCommon("Kawali Chibbi", R.drawable.fc8, R.drawable.face_kawali_chibbi_face, R.string.accessory_face8, 5))
            modelList.add(RC_Model_DataCommon("Long Laugh Smile", R.drawable.fc9, R.drawable.face_long_laugh_smile, R.string.accessory_face9, 5))
            modelList.add(RC_Model_DataCommon("Lower Lash Look", R.drawable.fc10, R.drawable.face_lower_lash_face, R.string.accessory_face10, 5))
        } else if ("head" == which) {
            modelList.add(RC_Model_DataCommon("Pastel Pink", R.drawable.hr1, R.drawable.head_big_hair_pastel_pink_bow, R.string.accessory_head1, 5))
            modelList.add(RC_Model_DataCommon("Goth Captain", R.drawable.hr2, R.drawable.head_black_goth_captain_hat, R.string.accessory_head2, 5))
            modelList.add(RC_Model_DataCommon("Brown Dog Ears", R.drawable.hr3, R.drawable.head_brown_dog_ears, R.string.accessory_head3, 5))
            modelList.add(RC_Model_DataCommon("Dark Camo", R.drawable.hr4, R.drawable.head_dark_camo_ushkana, R.string.accessory_head4, 5))
            modelList.add(RC_Model_DataCommon("Red Hat", R.drawable.hr5, R.drawable.head_hat, R.string.accessory_head5, 5))
            modelList.add(RC_Model_DataCommon("Pink-Brown Beret", R.drawable.hr6, R.drawable.head_pink_brown_beret, R.string.accessory_head6, 5))
            modelList.add(RC_Model_DataCommon("White Pulse", R.drawable.hr7, R.drawable.head_pink_white_pulse_bow, R.string.accessory_head7, 5))
            modelList.add(RC_Model_DataCommon("Unicorn", R.drawable.hr8, R.drawable.head_unicorn, R.string.accessory_head8, 5))
            modelList.add(RC_Model_DataCommon("White Headphone", R.drawable.hr9, R.drawable.head_white_headphone, R.string.accessory_head9, 5))
            modelList.add(RC_Model_DataCommon("Punk Cap", R.drawable.hr10, R.drawable.head_y2k_punk_cap, R.string.accessory_head10, 5))
        } else if ("neck" == which) {
            modelList.add(RC_Model_DataCommon("Black Chokker with Silver Cross", R.drawable.nk1, R.drawable.neck_black_choker_silver_cross, R.string.accessory_neck1, 5))
            modelList.add(RC_Model_DataCommon("Black Headphones", R.drawable.nk2, R.drawable.neck_black_headphones, R.string.accessory_neck2, 5))
            modelList.add(RC_Model_DataCommon("Cinnamorolla", R.drawable.nk3, R.drawable.neck_cinnamorollo, R.string.accessory_neck3, 5))
            modelList.add(RC_Model_DataCommon("Jade Necklace", R.drawable.nk4, R.drawable.neck_jade_necklace, R.string.accessory_neck4, 5))
            modelList.add(RC_Model_DataCommon("White-Pink Pearl Ribbon", R.drawable.nk5, R.drawable.neck_pearl_ribbon, R.string.accessory_neck5, 5))
            modelList.add(RC_Model_DataCommon("Stripped Scarf", R.drawable.nk6, R.drawable.neck_pink_stripped_scarf, R.string.accessory_neck6, 5))
            modelList.add(RC_Model_DataCommon("Beige Coloured Scarf", R.drawable.nk7, R.drawable.neck_plaid_beige_scarf, R.string.accessory_neck7, 5))
            modelList.add(RC_Model_DataCommon("Brown Coloured Scarf", R.drawable.nk8, R.drawable.neck_plaid_brown_scarf, R.string.accessory_neck8, 5))
            modelList.add(RC_Model_DataCommon("White Headphones", R.drawable.nk9, R.drawable.neck_white_headphones, R.string.accessory_neck9, 5))
            modelList.add(RC_Model_DataCommon("Silver Layered Necklace", R.drawable.nk10, R.drawable.neck_y2k_silver_layered_necklace, R.string.accessory_neck10, 5))
        }
        adapter = RC_Adapter_Accesso(this, modelList)
        binding.recyclerView.adapter = adapter
    }

    private fun loadCloth() {
        if ("Shoes Collection" == which) {
            binding.tvtitle.text = which
            modelList.add(RC_Model_DataCommon("Black Flip Flop", R.drawable.sc1, R.drawable.ft_adidas_black_flip_flop, R.string.ft_adidas_black_flip_flop, 5))
            modelList.add(RC_Model_DataCommon("Mules Silver", R.drawable.sc2, R.drawable.ft_adidas_mules_silver, R.string.ft_adidas_mules_silver, 5))
            modelList.add(RC_Model_DataCommon("Predator 24 Black", R.drawable.sc3, R.drawable.ft_adidas_predator_24, R.string.ft_adidas_predator_24_black, 5))
            modelList.add(RC_Model_DataCommon("Predator 24 Yellow Solar", R.drawable.sc4, R.drawable.ft_adidas_predator_24_yellow_solar, R.string.ft_adidas_predator_24_yellow_solar, 5))
            modelList.add(RC_Model_DataCommon("Predator 24 Greyish", R.drawable.sc5, R.drawable.ft_adidas_predator_24_greyish, R.string.ft_adidas_predator_24_greyish, 5))
            modelList.add(RC_Model_DataCommon("Adidas Robo", R.drawable.sc6, R.drawable.ft_adidas_robo_shoes, R.string.ft_adidas_robo, 5))
            modelList.add(RC_Model_DataCommon("Canvas Shoes Grey", R.drawable.sc7, R.drawable.ft_canvas_shoes_grey, R.string.ft_canvas_shoes_grey, 5))
            modelList.add(RC_Model_DataCommon("Canvas Shoes White", R.drawable.sc8, R.drawable.ft_canvas_shoes_white, R.string.ft_canvas_shoes_white, 5))
            modelList.add(RC_Model_DataCommon("Charli XCX Brat Heels", R.drawable.sc9, R.drawable.ft_charli_xcx_brat_heels, R.string.ft_charli_xcx_brat_heels, 5))
            modelList.add(RC_Model_DataCommon("Cozy Black Fur Boots", R.drawable.sc10, R.drawable.ft_cozy_black_fur_boots, R.string.ft_cozy_black_fur_boots, 5))
            modelList.add(RC_Model_DataCommon("Formal Dress Brown Shoes", R.drawable.sc11, R.drawable.ft_dress_shoes_brown, R.string.ft_dress_shoes_brown, 5))
            modelList.add(RC_Model_DataCommon("Flats Black", R.drawable.sc12, R.drawable.ft_flats_black, R.string.ft_flats_black, 5))
            modelList.add(RC_Model_DataCommon("Flats Zebra", R.drawable.sc13, R.drawable.ft_flats_zebra, R.string.ft_flats_zebra, 5))
            modelList.add(RC_Model_DataCommon("Flats Blue", R.drawable.sc14, R.drawable.ft_flats_blue, R.string.ft_flats_blue, 5))
            modelList.add(RC_Model_DataCommon("High Tops", R.drawable.sc15, R.drawable.ft_high_tops, R.string.ft_high_tops, 5))
            modelList.add(RC_Model_DataCommon("Huggo X FFS", R.drawable.sc16, R.drawable.ft_huggo_x_ffs, R.string.ft_huggo_x_ffs, 5))
            modelList.add(RC_Model_DataCommon("Military Boots", R.drawable.sc17, R.drawable.ft_militart_boots, R.string.ft_military_boots, 5))
            modelList.add(RC_Model_DataCommon("Ralph Lauren", R.drawable.sc18, R.drawable.ft_ralph_lauren, R.string.ft_ralph_lauren, 5))
            modelList.add(RC_Model_DataCommon("Weyes Blood", R.drawable.sc19, R.drawable.ft_weyes_blood, R.string.ft_weyes_blood, 5))
            modelList.add(RC_Model_DataCommon("Work Boots Brown", R.drawable.sc20, R.drawable.ft_work_boots_brown, R.string.ft_work_boots_brown, 5))
        } else if ("Pants Collection" == which) {
            binding.tvtitle.text = which
            modelList.add(RC_Model_DataCommon("Baggy Jeans", R.drawable.pc1, R.drawable.pant1, R.string.pant_baggy_jeans, 5))
            modelList.add(RC_Model_DataCommon("Black Cargo Outfit", R.drawable.pc2, R.drawable.pant2, R.string.pant_black_cargo_outfit, 5))
            modelList.add(RC_Model_DataCommon("Black Y2K Star Jeans", R.drawable.pc3, R.drawable.pant3, R.string.pant_black_y2k_star_jeans, 5))
            modelList.add(RC_Model_DataCommon("Blue Allien Sleep", R.drawable.pc4, R.drawable.pant4, R.string.pant_blue_alien_sleep_pant, 5))
            modelList.add(RC_Model_DataCommon("Blue Butterfly Pant", R.drawable.pc5, R.drawable.pant5, R.string.pant_blue_butterfly_pants, 5))
            modelList.add(RC_Model_DataCommon("Denim Cargo", R.drawable.pc6, R.drawable.pant6, R.string.pant_blue_denim_cargo_pant, 5))
            modelList.add(RC_Model_DataCommon("Denim Overall Outfit Pink", R.drawable.pc7, R.drawable.pant7, R.string.pant_denim_overall_outfit_pink, 5))
            modelList.add(RC_Model_DataCommon("Forbidden Full Black", R.drawable.pc8, R.drawable.pant8, R.string.pant_forbidden_full_black_pant, 5))
            modelList.add(RC_Model_DataCommon("Gangster Pant", R.drawable.pc9, R.drawable.pant9, R.string.pant_gangster_jeans, 5))
            modelList.add(RC_Model_DataCommon("Oversized Denim Overall Outfit", R.drawable.pc10, R.drawable.pant10, R.string.pant_oversized_denim_overall_outfit, 5))
            modelList.add(RC_Model_DataCommon("Payjama Pant", R.drawable.pc11, R.drawable.pant11, R.string.pant_pajama_pant, 5))
            modelList.add(RC_Model_DataCommon("Peach Cargo", R.drawable.pc12, R.drawable.pant12, R.string.pant_peach_cargo_pant, 5))
            modelList.add(RC_Model_DataCommon("Pink Baggy Cargo", R.drawable.pc13, R.drawable.pant13, R.string.pant_pink_baggy_cargo_pants, 5))
            modelList.add(RC_Model_DataCommon("Pink-White Cargo", R.drawable.pc14, R.drawable.pant14, R.string.pant_pink_white_y2k_cargo_pants, 5))
            modelList.add(RC_Model_DataCommon("Ripped Black Pant", R.drawable.pc15, R.drawable.pant15, R.string.pant_ripped_black_jeans, 5))
            modelList.add(RC_Model_DataCommon("Super light Baggy Jeans", R.drawable.pc16, R.drawable.pant16, R.string.pant_super_light_baggy_wash_jeans, 5))
            modelList.add(RC_Model_DataCommon("Urban Cargo", R.drawable.pc17, R.drawable.pant17, R.string.pant_urban_medium_cargo_jeans, 5))
            modelList.add(RC_Model_DataCommon("White Sweatpants", R.drawable.pc18, R.drawable.pant18, R.string.pant_white_sweatpants, 5))
            modelList.add(RC_Model_DataCommon("White Cargo", R.drawable.pc19, R.drawable.pant19, R.string.pant_white_y2k_cargo_pants, 5))
            modelList.add(RC_Model_DataCommon("Black Wide Cargo", R.drawable.pc20, R.drawable.pant20, R.string.pant_y2k_wide_cargo_black_pant, 5))
        } else if ("T-shirt Collection" == which) {
            binding.tvtitle.text = which
            modelList.add(RC_Model_DataCommon("Baggy Off-Band", R.drawable.tsc1, R.drawable.tee_baggy_off_band_shirt, R.string.tee_baggy_off_band_shirt, 5))
            modelList.add(RC_Model_DataCommon("Black Crop Top", R.drawable.tsc2, R.drawable.tee_black_crop_top, R.string.tee_black_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Black Silver Cross", R.drawable.tsc3, R.drawable.tee_black_silver_cross_top, R.string.tee_black_silver_cross_top, 5))
            modelList.add(RC_Model_DataCommon("Black Tank Top", R.drawable.tsc4, R.drawable.tee_black_tank_top, R.string.tee_black_tank_top, 5))
            modelList.add(RC_Model_DataCommon("Blue Tube Top", R.drawable.tsc5, R.drawable.tee_blue_tube_top, R.string.tee_blue_tube_top, 5))
            modelList.add(RC_Model_DataCommon("EMO", R.drawable.tsc6, R.drawable.tee_emo, R.string.tee_emo, 5))
            modelList.add(RC_Model_DataCommon("Heart Baby", R.drawable.tsc7, R.drawable.tee_heart_baby, R.string.tee_heart_baby, 5))
            modelList.add(RC_Model_DataCommon("Leopard Tank Top", R.drawable.tsc8, R.drawable.tee_leopard_tank_top, R.string.tee_leopard_tank_top, 5))
            modelList.add(RC_Model_DataCommon("Oversized Black", R.drawable.tsc9, R.drawable.tee_oversized_black, R.string.shirt_oversized_black_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Pink Crop Top", R.drawable.tsc10, R.drawable.tee_pink_crop_top, R.string.tee_pink_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Pink Ruffle Sleeves", R.drawable.tsc11, R.drawable.tee_pink_ruffle_sleeves, R.string.tee_pink_ruffle_sleeves, 5))
            modelList.add(RC_Model_DataCommon("Purple Top", R.drawable.tsc12, R.drawable.tee_purple, R.string.tee_purple, 5))
            modelList.add(RC_Model_DataCommon("Secret Service", R.drawable.tsc13, R.drawable.tee_secret_service, R.string.tee_secret_service, 5))
            modelList.add(RC_Model_DataCommon("Star Black Tee", R.drawable.tsc14, R.drawable.tee_star, R.string.tee_star, 5))
            modelList.add(RC_Model_DataCommon("Tube Black", R.drawable.tsc15, R.drawable.tee_tube_top_black, R.string.tee_tube_top_black, 5))
            modelList.add(RC_Model_DataCommon("White", R.drawable.tsc16, R.drawable.tee_white, R.string.tee_white, 5))
            modelList.add(RC_Model_DataCommon("White Bow Top", R.drawable.tsc17, R.drawable.tee_white_bow_top, R.string.tee_white_bow_top, 5))
            modelList.add(RC_Model_DataCommon("White Crop Top", R.drawable.tsc18, R.drawable.tee_white_crop_top, R.string.tee_white_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Y2K Star Baby", R.drawable.tsc19, R.drawable.tee_y2k_star_baby_tee, R.string.tee_y2k_star_baby_tee, 5))
            modelList.add(RC_Model_DataCommon("Y2K California Crop Top", R.drawable.tsc20, R.drawable.tee_y2k_california_crop_top, R.string.tee_y2k_california_crop_top, 5))
        } else if ("Shirts Collection" == which) {
            binding.tvtitle.text = which
            modelList.add(RC_Model_DataCommon("Adidas Knitted Top", R.drawable.sh1, R.drawable.shirt_adidas_knitted_top, R.string.shirt_adidas_knitted_top, 5))
            modelList.add(RC_Model_DataCommon("Ardor Open Zip Hoodie Black", R.drawable.sh2, R.drawable.shirt_ardor_open_zip_hoodie, R.string.shirt_ardor_open_zip_hoodie, 5))
            modelList.add(RC_Model_DataCommon("Ardor Open Zip Hoodie Grey", R.drawable.sh3, R.drawable.shirt_ardor_open_zip_hoodie_grey, R.string.shirt_ardor_open_zip_hoodie_grey, 5))
            modelList.add(RC_Model_DataCommon("Bandana Top", R.drawable.sh4, R.drawable.shirt_bandana_top_black, R.string.shirt_bandana_top_black, 5))
            modelList.add(RC_Model_DataCommon("Black Cropped Zip Hoodie", R.drawable.sh5, R.drawable.shirt_black_cropped_zip_hoodie, R.string.shirt_black_cropped_zip_hoodie, 5))
            modelList.add(RC_Model_DataCommon("Black Trendy School Hoodie", R.drawable.sh6, R.drawable.shirt_black_trendy_scholl_top, R.string.shirt_black_trendy_school_top, 5))
            modelList.add(RC_Model_DataCommon("B/W Bandage Top", R.drawable.sh7, R.drawable.shirt_black_white_bandage_top, R.string.shirt_black_white_bandage_top, 5))
            modelList.add(RC_Model_DataCommon("Blue Bandage Crop Top", R.drawable.sh8, R.drawable.shirt_blue_bandage_crop_top, R.string.shirt_blue_bandage_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Cute Summer Top", R.drawable.sh9, R.drawable.shirt_cute_summer_crop_top, R.string.shirt_cute_summer_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Denim Tears Hoodie", R.drawable.sh10, R.drawable.shirt_denim_tears_hoodie, R.string.shirt_denim_tears_hoodie, 5))
            modelList.add(RC_Model_DataCommon("Leopard Folded Top", R.drawable.sh11, R.drawable.shirt_leopard_folded_top, R.string.shirt_leopard_folded_top, 5))
            modelList.add(RC_Model_DataCommon("Leopard Tied Top", R.drawable.sh12, R.drawable.shirt_leopard_print_tied_top, R.string.shirt_leopard_print_tied_top, 5))
            modelList.add(RC_Model_DataCommon("Miami Off-Shoulder White", R.drawable.sh13, R.drawable.shirt_miami_off_shoulder, R.string.shirt_miami_off_shoulder, 5))
            modelList.add(RC_Model_DataCommon("Miami Off-Shoulder Black", R.drawable.sh14, R.drawable.shirt_miami_off_shoulder_black, R.string.shirt_miami_off_shoulder_black, 5))
            modelList.add(RC_Model_DataCommon("Oversized Black", R.drawable.sh15, R.drawable.shirt_oversized_black_crop_top, R.string.shirt_oversized_black_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Oversized White", R.drawable.sh16, R.drawable.shirt_oversized_white_crop_top, R.string.shirt_oversized_white_crop_top, 5))
            modelList.add(RC_Model_DataCommon("Oxford Off-Shoulder", R.drawable.sh17, R.drawable.shirt_oxford_off_shoulder, R.string.shirt_oxford_off_shoulder, 5))
            modelList.add(RC_Model_DataCommon("Striped Oversized", R.drawable.sh18, R.drawable.shirt_striped_oversized_top, R.string.shirt_striped_oversized_top, 5))
            modelList.add(RC_Model_DataCommon("White Oversized Sweater", R.drawable.sh19, R.drawable.shirt_white_oversized_sweater, R.string.shirt_white_oversized_sweater, 5))
            modelList.add(RC_Model_DataCommon("Christmas Outfit", R.drawable.sh20, R.drawable.shirt_y2k_red_paid_christmas_outfit, R.string.shirt_y2k_red_paid_christmas_outfit, 5))
        }
        adapter = RC_Adapter_Accesso(this, modelList)
        binding.recyclerView.adapter = adapter
    }

    private fun loadHead() {
        if ("Face Look" == which) {
            binding.tvtitle.text = which
            modelList.add(RC_Model_DataCommon("Angry", R.drawable.fl1, R.drawable.face_angry, R.string.face_angry, 5))
            modelList.add(RC_Model_DataCommon("Cute Smile", R.drawable.fl2, R.drawable.face_cute_smile, R.string.face_cute_smile, 5))
            modelList.add(RC_Model_DataCommon("Eye Blink", R.drawable.fl3, R.drawable.face_eye_blink, R.string.face_eye_blink, 5))
            modelList.add(RC_Model_DataCommon("Look At My Nose", R.drawable.fl4, R.drawable.face_look_at_my_nose, R.string.face_look_at_my_nose, 5))
            modelList.add(RC_Model_DataCommon("Scared", R.drawable.fl5, R.drawable.face_scared, R.string.face_scared, 5))
            modelList.add(RC_Model_DataCommon("Sigmund", R.drawable.fl6, R.drawable.face_sigmund, R.string.face_sigmund, 5))
            modelList.add(RC_Model_DataCommon("Silly Fun", R.drawable.fl7, R.drawable.face_silly_fun, R.string.face_silly_fun, 5))
            modelList.add(RC_Model_DataCommon("Sly Guy", R.drawable.fl8, R.drawable.face_sly_guy_face, R.string.face_sly_guy_face, 5))
            modelList.add(RC_Model_DataCommon("Teeth Smile", R.drawable.fl9, R.drawable.face_teeth_smile, R.string.face_teeth_smile, 5))
            modelList.add(RC_Model_DataCommon("Tongy Smile", R.drawable.fl10, R.drawable.face_tongy_smile, R.string.face_tongy_smile, 5))
        } else if ("Hair Accessories" == which) {
            binding.tvtitle.text = which
            modelList.add(RC_Model_DataCommon("Braided", R.drawable.ha1, R.drawable.hair_1, R.string.hair_barided, 5))
            modelList.add(RC_Model_DataCommon("Black Layered", R.drawable.ha2, R.drawable.hair_2, R.string.hair_black_layered, 5))
            modelList.add(RC_Model_DataCommon("Fluffy White", R.drawable.ha3, R.drawable.hair_3, R.string.hair_fluffy_white, 5))
            modelList.add(RC_Model_DataCommon("Layered Wavy Valley", R.drawable.ha4, R.drawable.hair_4, R.string.hair_layered_wavy_valley_girl, 5))
            modelList.add(RC_Model_DataCommon("Medium Middle Part", R.drawable.ha5, R.drawable.hair_5, R.string.hair_medium_middle_part, 5))
            modelList.add(RC_Model_DataCommon("Orange Beanie With Black Hair", R.drawable.ha6, R.drawable.hair_6, R.string.hair_orange_beanie_with_black, 5))
            modelList.add(RC_Model_DataCommon("Pony Tail", R.drawable.ha7, R.drawable.hair_7, R.string.hair_pony_tail, 5))
            modelList.add(RC_Model_DataCommon("Rainbow Coloured Hair", R.drawable.ha8, R.drawable.hair_8, R.string.hair_rainbow_coloured, 5))
            modelList.add(RC_Model_DataCommon("Valentines", R.drawable.ha9, R.drawable.hair_9, R.string.hair_valentines, 5))
            modelList.add(RC_Model_DataCommon("Wavy Middle Part", R.drawable.ha10, R.drawable.hair_10, R.string.hair_wavy_middle_part, 5))
        } else if ("Face Shape" == which) {
            binding.tvtitle.text = which
            modelList.add(RC_Model_DataCommon("Cheeks", R.drawable.fs1, R.drawable.head_cheeks, R.string.head_cheeks, 5))
            modelList.add(RC_Model_DataCommon("Chiseled", R.drawable.fs2, R.drawable.head_chiseled, R.string.head_chiseled, 5))
            modelList.add(RC_Model_DataCommon("Narrow", R.drawable.fs3, R.drawable.head_narrow, R.string.head_narrow, 5))
            modelList.add(RC_Model_DataCommon("Paragon", R.drawable.fs4, R.drawable.head_paragon, R.string.head_paragon, 5))
            modelList.add(RC_Model_DataCommon("Strong Jaw", R.drawable.fs5, R.drawable.head_strong_jaw, R.string.head_strong_jaw, 5))
            modelList.add(RC_Model_DataCommon("Trim", R.drawable.fs6, R.drawable.head_trim, R.string.head_trim, 5))
        }
        adapter = RC_Adapter_Accesso(this, modelList)
        binding.recyclerView.adapter = adapter
    }
}
