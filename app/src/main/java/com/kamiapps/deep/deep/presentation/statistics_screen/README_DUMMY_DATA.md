# Dummy Data System for Statistics Screen

## Overview
A comprehensive dummy data system has been implemented to test the Statistics Screen functionality without requiring real user session data.

## How to Enable/Disable

### Quick Toggle
Change the constant in `StatisticsViewModel.kt`:
```kotlin
private const val USE_DUMMY_DATA = true  // Set to false for real data
```

### What Gets Replaced
When `USE_DUMMY_DATA = true`:
- ✅ Chart data for all time periods (Day/Week/Month/Year)
- ✅ Summary statistics (total focus time, sessions, averages)
- ✅ Top tags list with realistic usage counts
- ✅ Session logs with varied activities and times
- ✅ Tag carousel with colorful sample tags

## Dummy Data Categories

### 📊 Chart Data
- **HourlyFocusData**: Realistic productivity curve (6am-11pm)
- **DailyFocusData**: Weekly pattern with weekend dips
- **MonthlyFocusData**: 31 days with weekend/weekday variations
- **YearlyFocusData**: Seasonal patterns (vacation months lower)
- **WeekdayFocusData**: Classic "Wednesday peak" productivity

### 📈 Statistics
- **Total Focus Time**: 178h 45m
- **Total Sessions**: 156
- **Average Duration**: 2h 18m

### 🏷️ Tags & Activities
- Programming, Reading, Writing, Research, Learning, Design, Music, Exercise, Meditation, Cooking
- Each with unique emoji and color scheme
- Realistic usage distribution

### 📝 Session Logs
- 15 recent sessions across different activities
- Realistic time ranges and dates
- Varied durations (1h-4h range)

## Testing Scenarios

### Day View Testing
- Shows 24-hour productivity curve
- Peak at 15:00 (3 PM)
- Natural early morning and evening drops

### Week View Testing  
- Friday highest (240 min), weekends lower
- Progressive weekday build-up
- Shows both daily bars and hourly line chart

### Month View Testing
- 31 days of varied data
- Weekend patterns visible
- Multiple chart types in sequence

### Year View Testing
- 12 months with seasonal variations
- Summer months show vacation dips
- Horizontal scrolling enabled for yearly chart

## Benefits
- ✅ **Immediate Testing**: No need to create real sessions
- ✅ **Realistic Patterns**: Data mimics actual user behavior
- ✅ **Complete Coverage**: All chart types and time periods
- ✅ **Visual Validation**: Easy to spot UI/UX issues
- ✅ **Performance Testing**: Charts handle realistic data volumes

## Cleanup
Before production:
1. Set `USE_DUMMY_DATA = false` in `StatisticsViewModel.kt`
2. Remove dummy data related code blocks
3. Delete this README file

## Data Patterns Used
- **Morning Peak**: 9-10 AM productive start
- **Afternoon Peak**: 2-3 PM post-lunch productivity
- **Evening Drop**: Natural decline after 6 PM
- **Wednesday Peak**: Mid-week productivity maximum
- **Weekend Dip**: Lower focus time on Sat/Sun
- **Seasonal Variation**: Summer months show vacation impact 