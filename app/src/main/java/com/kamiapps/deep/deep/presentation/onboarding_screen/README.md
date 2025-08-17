# Onboarding Screen - Kullanım Kılavuzu

## Genel Bakış
Bu onboarding screen sistemi generic ve MVVM mimarisi kullanılarak tasarlanmıştır. 4 sayfalık bir onboarding deneyimi sunar.

## Dosya Yapısı
```
onboarding_screen/
├── OnboardingScreen.kt          # Ana composable
├── OnboardingViewModel.kt       # ViewModel (MVVM)
├── OnboardingUiState.kt        # UI State data classes
├── OnboardingActions.kt        # User actions
└── components/
    ├── OnboardingPageContent.kt    # Sayfa içeriği
    ├── OnboardingIndicator.kt      # Sayfa göstergesi
    └── OnboardingButton.kt         # Buton bileşenleri
```

## Özellikler
- ✅ MVVM mimarisi
- ✅ Generic ve yeniden kullanılabilir
- ✅ 4 sayfalık onboarding
- ✅ Smooth geçiş animasyonları
- ✅ Floating icons animasyonu (ilk sayfa)
- ✅ Page indicator
- ✅ Skip functionality
- ✅ Responsive tasarım

## Kullanım

### 1. Navigation'a Ekleme
```kotlin
// RootNavigationGraph.kt dosyanızda
composable("onboarding") {
    OnboardingScreen(
        onNavigateToHome = {
            navController.navigate("home") {
                popUpTo("onboarding") { inclusive = true }
            }
        }
    )
}
```

### 2. Icon'ları Ekleme
`OnboardingViewModel.kt` dosyasında, `floatingIcons` listesine icon resource'larınızı ekleyin:

```kotlin
floatingIcons = listOf(
    R.drawable.ic_butterfly,
    R.drawable.ic_stopwatch,
    R.drawable.ic_calculator,
    R.drawable.ic_stack,
    R.drawable.ic_image
)
```

### 3. Ana Logo Ekleme
`OnboardingPageContent.kt` dosyasında, TODO kısmını kendi logonuzla değiştirin:

```kotlin
// TODO kısmını bulun ve değiştirin
Image(
    painter = painterResource(id = R.drawable.ic_main_logo),
    contentDescription = "App Logo",
    modifier = Modifier.size(120.dp)
)
```

### 4. Sayfa İçeriklerini Özelleştirme
`OnboardingViewModel.kt` dosyasında sayfa içeriklerini düzenleyebilirsiniz:

```kotlin
OnboardingPage(
    title = "Özel Başlık",
    description = "Özel açıklama metni",
    iconResource = R.drawable.your_icon, // Opsiyonel
    showFloatingIcons = false
)
```

## Animasyonlar
- **Floating Icons**: İlk sayfada icon'lar daire şeklinde döner
- **Page Transitions**: Sayfalar arası smooth geçişler
- **Button Animations**: Buton durumları için fade animasyonları
- **Indicator**: Aktif sayfa göstergesi animasyonu

## Gereksinimler
- Compose BOM
- Hilt (Dependency Injection)
- ViewModel Compose
- Navigation Compose

## Özelleştirme
- Sayfa sayısını değiştirmek için `OnboardingUiState.kt`'de `totalPages` değerini değiştirin
- Renkleri özelleştirmek için `OnboardingPage.backgroundColor` kullanın
- Animasyon sürelerini `OnboardingPageContent.kt`'de düzenleyebilirsiniz

## Notlar
- Icon'lar kullanıcı tarafından ekleneceği için şu anda placeholder olarak gösterilmektedir
- Onboarding tamamlandığında otomatik olarak ana sayfaya yönlendirir
- SharedPreferences ile onboarding durumu kaydedilebilir (ViewModel'de yorum olarak belirtilmiş) 