package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandGreenPrimary
import com.example.ui.theme.BrandGreenSecondary
import com.example.ui.theme.BrandTextPrimary
import com.example.ui.theme.BrandTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    isArabic: Boolean,
    onOnboardingComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var agreedToTerms by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(BrandGreenPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AUNIO.AI",
                    color = BrandTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 3.sp
                )
            }

            // Horizontal Pager for Onboarding Steps
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (page) {
                        0 -> {
                            Text(
                                text = if (isArabic) "مرحباً بك في AUNIO.AI" else "Welcome to AUNIO.AI",
                                color = BrandTextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("onboarding_title_1")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isArabic)
                                    "مساعدك الشخصي الذكي الذي يتعلم كيفية مساعدتك بشكل أفضل بمرور الوقت."
                                else
                                    "Your intelligent personal assistant that learns how to help you better over time.",
                                color = BrandTextSecondary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                        1 -> {
                            Text(
                                text = if (isArabic) "تجربة مخصصة لك" else "Personalized Experience",
                                color = BrandTextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("onboarding_title_2")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isArabic)
                                    "يتذكر AUNIO.AI المعلومات المفيدة التي تختار مشاركتها أثناء المحادثات حتى تبدو الدردشات المستقبلية أكثر طبيعية ومخصصة."
                                else
                                    "AUNIO.AI remembers useful information you choose to share during conversations so future chats feel more natural and personalized.",
                                color = BrandTextSecondary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                        2 -> {
                            Text(
                                text = if (isArabic) "خصوصيتك تأتي أولاً" else "Your Privacy Comes First",
                                color = BrandTextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("onboarding_title_3")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isArabic)
                                    "معلوماتك الشخصية ملك لك وحدك.\nيمكنك مراجعة ذكرياتك أو تعديلها أو حذفها في أي وقت من الإعدادات."
                                else
                                    "Your personal information belongs to you.\nYou can review, edit, or delete your memories at any time from Settings.",
                                color = BrandTextSecondary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }

            // Bottom Navigation, Indicators & Actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    repeat(3) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) BrandGreenPrimary else BrandTextSecondary.copy(alpha = 0.3f))
                        )
                    }
                }

                // Terms Checkbox (Visible on Page 3)
                AnimatedVisibility(
                    visible = pagerState.currentPage == 2,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                            .clickable { agreedToTerms = !agreedToTerms },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Custom Checkbox
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (agreedToTerms) BrandGreenPrimary else BrandTextSecondary,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    color = if (agreedToTerms) BrandGreenPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (agreedToTerms) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Checked",
                                    tint = BrandBackground,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isArabic)
                                "أوافق على شروط الخدمة وسياسة الخصوصية."
                            else
                                "I agree to the Terms of Service and Privacy Policy.",
                            color = BrandTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Next / Continue Button
                val isLastPage = pagerState.currentPage == 2
                val buttonEnabled = !isLastPage || agreedToTerms

                Button(
                    onClick = {
                        if (isLastPage) {
                            if (agreedToTerms) {
                                onOnboardingComplete()
                            }
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    enabled = buttonEnabled,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (buttonEnabled) BrandGreenPrimary else BrandCard,
                        contentColor = if (buttonEnabled) BrandBackground else BrandTextSecondary.copy(alpha = 0.5f),
                        disabledContainerColor = BrandCard,
                        disabledContentColor = BrandTextSecondary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("onboarding_continue_button")
                ) {
                    Text(
                        text = if (isLastPage) {
                            if (isArabic) "بدء الاستخدام" else "Continue"
                        } else {
                            if (isArabic) "التالي" else "Next"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isLastPage) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next page",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
