# Media Story Generator

A Java application that transforms a folder of photos and videos into a narrated, normalized video story — powered by Gemini AI, Google Translate TTS, GeoApify Maps, and FFmpeg.

---

## What It Does

1. **Scans** a folder of media files (images and videos)
2. **Sorts** them chronologically by EXIF metadata
3. **Describes** each file using the Gemini AI API
4. **Generates TTS audio** for each description via Google Translate
5. **Tries to create a summary image** representing the entire story (Gemini 2.5 may fail) - Probably requires Billing
6. **Generates a map** with pins at the first and last GPS locations
7. **Generates an inspirational quote** based on the descriptions
8. **Assembles everything** into a portrait video (1080x1920) with:
   - Summary image (intro) - No Guarantee
   - Each media file with its descriptive narration
   - Map slide
   - Quote slide (outro)
9. **Normalizes audio** to YouTube standards (EBU R128)

---


### API Keys
| Service | Free Tier | Used For |
|---|---|---|
| [Google Gemini](https://aistudio.google.com) | 1,500 req/day | Descriptions, summary image, quote |
| [GeoApify](https://myprojects.geoapify.com) | 3,000 req/day | Static map generation |
| Google Translate TTS | Unlimited (unofficial) | Text-to-speech audio |

---

## Configuration

In `Main.java`, set your keys and input folder:

```java
final String GeminiAPIKey   = "GeminiAPIGoesHere";
final String GeoApifyAPIKey = "GeoApifyKeyGoesHere";
String folderPath = "Partials/Partial2/MediaInput"; // Modify to match MediaInput
```

---

## How to Run

1. Place your media files (`.jpg`, `.png`, `.mp4`) in `/MediaInput/`
2. Set your API keys in `Main.java`
3. Run `Main.java`
4. Find your final video at `output_normalized.mp4`

---

## Output Video Structure

| Segment | Content | Audio |
|---|---|---|
| Intro | AI-generated summary image | None |
| 1..N | Each media file (sorted by date) | TTS narration of Gemini description |
| Map | GeoApify map with Start/End pins | None |
| Outro | Black slide with inspirational quote | TTS narration of quote |

All segments are portrait format (1080×1920) — optimized for mobile and YouTube Shorts.

---

## Audio Standards

The final video is normalized to YouTube's recommended loudness targets:

| Parameter | Target |
|---|---|
| Loudness (LUFS) | -14 LUFS |
| True Peak (dBTP) | -1 dBTP |
| LRA (LU) | 7 LU |

Normalization uses a two-pass EBU R128 process via FFmpeg's `loudnorm` filter.

---

## Known Limitations

- **Gemini free tier** hits quota limits under high demand (1,500 req/day). If descriptions fail, the app retries 3 times then falls back to `"Failed to get a valid response"` — TTS will generate silence for those segments.
- **Summary image generation** requires Gemini 2.0 Flash image preview model (Billing). If unavailable, the video starts directly with the first media file.
- **GPS map** only generates if input media contains EXIF GPS data. Images from WhatsApp or screenshots typically have no GPS tags.
- **Google Translate TTS** has an unofficial ~200 character limit per request — long descriptions are automatically chunked and concatenated.
- **Windows only** — all `ProcessBuilder` calls use `cmd.exe`. Linux/macOS would require removing `"cmd.exe", "/c"` from each command.

---

## Troubleshooting

| Symptom | Likely Cause | Fix |
|---|---|---|
| `ResourceExhausted` from Gemini | Daily quota hit | Wait until midnight Pacific time |
| `No description found` | Gemini returned unexpected JSON | Print raw response to diagnose |
| Silent segments in video | TTS failed or description was empty | Check `audio_N.mp3` files exist and have duration |
| `map.jpg` not generated | GeoApify key invalid or no GPS in media | Verify key at myprojects.geoapify.com |
| `Pass 1 failed` in normalization | `output.mp4` has no audio track | Ensure at least one segment has valid audio |
| Quote not appearing | Gemini unavailable | Fallback quote is used automatically |

---

## Notes

- Media is renamed to `000.jpg`, `001.jpg`, etc. in `ordered_media/` — original files are untouched.
- `descriptions.json` is saved after each run — delete it to force re-description on next run.
- Thread sleeps between API calls (`2000ms` for Gemini, `1200ms` for TTS) are intentional to avoid rate limiting.