package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;
import net.runelite.api.SoundEffectID;
import net.runelite.client.config.FontType;
import net.runelite.client.config.Keybind;
import net.runelite.client.ui.overlay.OverlayLayer;
import org.junit.Test;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

/**
 * Test library/framework: JUnit (aligns with existing project usage).
 * These tests validate default values, constants, and enum selections defined in WatchdogConfig.
 * We instantiate an anonymous implementation to invoke default methods.
 */
public class WatchdogConfigTest {

    private final WatchdogConfig cfg = new WatchdogConfig() { };

    // ---- Constants ----
    @Test
    public void configGroupName_isWatchdog() {
        assertEquals("watchdog", WatchdogConfig.CONFIG_GROUP_NAME);
    }

    @Test
    public void defaultNotificationTextColor_isWhite() {
        assertEquals(Color.WHITE, WatchdogConfig.DEFAULT_NOTIFICATION_TEXT_COLOR);
    }

    @Test
    public void defaultNotificationColor_matchesHex_46FF0000() {
        Color c = WatchdogConfig.DEFAULT_NOTIFICATION_COLOR;
        assertNotNull(c);
        assertEquals(255, c.getRed());
        assertEquals(0, c.getGreen());
        assertEquals(0, c.getBlue());
        assertEquals(0x46, c.getAlpha());
    }

    // ---- Hidden / serialization defaults ----
    @Test
    public void alerts_defaultIsEmptyArrayString() {
        assertEquals("[]", cfg.alerts());
    }

    @Test
    public void pluginVersion_defaultIsNull() {
        assertNull(cfg.pluginVersion());
    }

    // ---- Core toggles ----
    @Test
    public void ttsEnabled_defaultFalse() {
        assertFalse(cfg.ttsEnabled());
    }

    @Test
    public void mouseMovementCancels_defaultTrue() {
        assertTrue(cfg.mouseMovementCancels());
    }

    @Test
    public void overrideImportsWithDefaults_defaultFalse() {
        assertFalse(cfg.overrideImportsWithDefaults());
    }

    @Test
    public void putSoundsIntoQueue_defaultTrue() {
        assertTrue(cfg.putSoundsIntoQueue());
    }

    @Test
    public void sidePanelPriority_defaultOne_andWithinIntRange() {
        int v = cfg.sidePanelPriority();
        assertEquals(1, v);
        // Sanity: ensure not underflowing given @Range(min = Integer.MIN_VALUE)
        assertTrue(v >= Integer.MIN_VALUE);
    }

    @Test
    public void enableNotificationCategories_defaultTrue() {
        assertTrue(cfg.enableNotificationCategories());
    }

    // ---- AFK ----
    @Test
    public void defaultAFKMode_defaultFalse() {
        assertFalse(cfg.defaultAFKMode());
    }

    @Test
    public void defaultAFKSeconds_defaultFive_andAtLeastOne() {
        int s = cfg.defaultAFKSeconds();
        assertEquals(5, s);
        assertTrue("Seconds should be >= 1 per @Range", s >= 1);
    }

    // ---- Overhead ----
    @Test
    public void defaultOverHeadDisplayTime_defaultThreeSeconds() {
        assertEquals(3, cfg.defaultOverHeadDisplayTime());
    }

    // ---- Overlay ----
    @Test
    public void overlayLayer_defaultAboveWidgets() {
        assertEquals(OverlayLayer.ABOVE_WIDGETS, cfg.overlayLayer());
    }

    @Test
    public void overlayFontType_defaultBold() {
        assertEquals(FontType.BOLD, cfg.overlayFontType());
    }

    @Test
    public void overlayShowTime_defaultTrue() {
        assertTrue(cfg.overlayShowTime());
    }

    @Test
    public void defaultOverlaySticky_defaultFalse() {
        assertFalse(cfg.defaultOverlaySticky());
    }

    @Test
    public void defaultOverlayTTL_defaultFiveSeconds() {
        assertEquals(5, cfg.defaultOverlayTTL());
    }

    @Test
    public void defaultOverlayTextColor_defaultsToNotificationTextColor() {
        assertEquals(WatchdogConfig.DEFAULT_NOTIFICATION_TEXT_COLOR, cfg.defaultOverlayTextColor());
    }

    @Test
    public void defaultOverlayColor_defaultsToNotificationColor() {
        assertEquals(WatchdogConfig.DEFAULT_NOTIFICATION_COLOR, cfg.defaultOverlayColor());
    }

    @Test
    public void defaultOverlayImagePath_defaultEmptyString() {
        assertEquals("", cfg.defaultOverlayImagePath());
    }

    // ---- Popup ----
    @Test
    public void defaultPopupTextColor_defaultNull() {
        assertNull(cfg.defaultPopupTextColor());
    }

    // ---- Screen Flash ----
    @Test
    public void defaultScreenFlashColor_defaultsToNotificationColor() {
        assertEquals(WatchdogConfig.DEFAULT_NOTIFICATION_COLOR, cfg.defaultScreenFlashColor());
    }

    @Test
    public void defaultScreenFlashType_defaultSolidTwoSeconds() {
        assertEquals(FlashNotification.SOLID_TWO_SECONDS, cfg.defaultScreenFlashType());
    }

    @Test
    public void defaultScreenFlashMode_defaultFlash() {
        assertEquals(FlashMode.FLASH, cfg.defaultScreenFlashMode());
    }

    @Test
    public void defaultScreenFlashDuration_defaultTwoSeconds() {
        assertEquals(2, cfg.defaultScreenFlashDuration());
    }

    // ---- Sound (custom path) ----
    @Test
    public void defaultSoundVolume_defaultEight_within0to10() {
        int vol = cfg.defaultSoundVolume();
        assertEquals(8, vol);
        assertTrue(vol >= 0 && vol <= 10);
    }

    @Test
    public void defaultSoundPath_defaultNull() {
        assertNull(cfg.defaultSoundPath());
    }

    // ---- Sound Effect ----
    @Test
    public void defaultSoundEffectID_defaultGEOfferDingaling() {
        assertEquals(SoundEffectID.GE_ADD_OFFER_DINGALING, cfg.defaultSoundEffectID());
    }

    @Test
    public void defaultSoundEffectVolume_defaultEight_within0to10() {
        int vol = cfg.defaultSoundEffectVolume();
        assertEquals(8, vol);
        assertTrue(vol >= 0 && vol <= 10);
    }

    // ---- TTS ----
    @Test
    public void defaultTTSVolume_defaultFive_within0to10() {
        int vol = cfg.defaultTTSVolume();
        assertEquals(5, vol);
        assertTrue(vol >= 0 && vol <= 10);
    }

    @Test
    public void defaultTTSSource_defaultElevenLabs() {
        assertEquals(TTSSource.ELEVEN_LABS, cfg.defaultTTSSource());
    }

    @Test
    public void defaultTTSVoice_defaultGeorge() {
        assertEquals(Voice.GEORGE, cfg.defaultTTSVoice());
    }

    @Test
    public void defaultTTSRate_defaultOne_within1to5() {
        int rate = cfg.defaultTTSRate();
        assertEquals(1, rate);
        assertTrue(rate >= 1 && rate <= 5);
    }

    @Test
    public void elevenLabsAPIKey_defaultEmptyString() {
        assertEquals("", cfg.elevenLabsAPIKey());
    }

    @Test
    public void defaultElevenLabsVoice_defaultNull() {
        assertNull(cfg.defaultElevenLabsVoice());
    }

    // ---- Request Focus ----
    @Test
    public void defaultRequestFocusForce_defaultFalse() {
        assertFalse(cfg.defaultRequestFocusForce());
    }

    // ---- Markers: Screen marker defaults ----
    @Test
    public void defaultScreenMarkerBorderColor_green() {
        assertEquals(Color.GREEN, cfg.defaultScreenMarkerBorderColor());
    }

    @Test
    public void defaultScreenMarkerFillColor_null() {
        assertNull(cfg.defaultScreenMarkerFillColor());
    }

    @Test
    public void defaultScreenMarkerBorderThickness_two() {
        assertEquals(2, cfg.defaultScreenMarkerBorderThickness());
    }

    @Test
    public void defaultScreenMarkerDisplayTime_fiveSeconds() {
        assertEquals(5, cfg.defaultScreenMarkerDisplayTime());
    }

    @Test
    public void defaultScreenMarkerSticky_false() {
        assertFalse(cfg.defaultScreenMarkerSticky());
    }

    // ---- Markers: Object marker defaults ----
    @Test
    public void defaultObjectMarkerBorderColor_yellow() {
        assertEquals(Color.YELLOW, cfg.defaultObjectMarkerBorderColor());
    }

    @Test
    public void defaultObjectMarkerFillColor_null() {
        assertNull(cfg.defaultObjectMarkerFillColor());
    }

    @Test
    public void defaultObjectMarkerHull_true() {
        assertTrue(cfg.defaultObjectMarkerHull());
    }

    @Test
    public void defaultObjectMarkerOutline_false() {
        assertFalse(cfg.defaultObjectMarkerOutline());
    }

    @Test
    public void defaultObjectMarkerClickbox_false() {
        assertFalse(cfg.defaultObjectMarkerClickbox());
    }

    @Test
    public void defaultObjectMarkerTile_false() {
        assertFalse(cfg.defaultObjectMarkerTile());
    }

    @Test
    public void defaultObjectMarkerBorderThickness_doubleTwo() {
        assertEquals(2.0d, cfg.defaultObjectMarkerBorderThickness(), 0.000001d);
    }

    @Test
    public void defaultObjectMarkerFeather_zeroWithin0to4() {
        int f = cfg.defaultObjectMarkerFeather();
        assertEquals(0, f);
        assertTrue(f >= 0 && f <= 4);
    }

    @Test
    public void defaultObjectMarkerDisplayTime_fiveSeconds() {
        assertEquals(5, cfg.defaultObjectMarkerDisplayTime());
    }

    @Test
    public void defaultObjectMarkerSticky_false() {
        assertFalse(cfg.defaultObjectMarkerSticky());
    }

    // ---- Hotkeys ----
    @Test
    public void clearAllHotkey_ctrlShiftW() {
        Keybind kb = cfg.clearAllHotkey();
        assertNotNull(kb);
        // Validate via equals against a newly created Keybind to avoid reliance on accessors
        Keybind expected = new Keybind(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        assertEquals(expected, kb);
    }

    @Test
    public void stopAllProcessingAlertsHotkey_notSet() {
        assertEquals(Keybind.NOT_SET, cfg.stopAllProcessingAlertsHotkey());
    }

    @Test
    public void stopAllQueuedSoundsHotkey_notSet() {
        assertEquals(Keybind.NOT_SET, cfg.stopAllQueuedSoundsHotkey());
    }

    @Test
    public void dismissAllOverlaysHotkey_notSet() {
        assertEquals(Keybind.NOT_SET, cfg.dismissAllOverlaysHotkey());
    }

    @Test
    public void dismissAllScreenMarkersHotkey_notSet() {
        assertEquals(Keybind.NOT_SET, cfg.dismissAllScreenMarkersHotkey());
    }

    @Test
    public void dismissAllObjectMarkersHotkey_notSet() {
        assertEquals(Keybind.NOT_SET, cfg.dismissAllObjectMarkersHotkey());
    }
}