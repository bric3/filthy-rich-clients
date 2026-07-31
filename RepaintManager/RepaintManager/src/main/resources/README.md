# Test card video

`test-card-h265.mp4` is generated from FFmpeg's built-in animated `testsrc2`
source. It contains no downloaded footage or audio. FFmpeg is only needed to regenerate the resource; running the demo
requires VLC/LibVLC instead.

From this directory, generate a new test card with:

```bash
ffmpeg \
  -y \
  -hide_banner \
  -loglevel warning \
  -f lavfi \
  -i "testsrc2=size=320x180:rate=24" \
  -frames:v 96 \
  -an \
  -c:v libx265 \
  -preset slow \
  -crf 30 \
  -pix_fmt yuv420p \
  -tag:v hvc1 \
  -movflags +faststart \
  -metadata title="Filthy Rich Clients test card" \
  -x265-params "log-level=error:pools=2:frame-threads=2" \
  test-card-h265.mp4
```

This produces a four-second, 320 by 180 pixel, 24 FPS HEVC/H.265 video. Its size may vary with the FFmpeg and libx265
versions, so check that it remains below 1 MiB:

```bash
wc -c test-card-h265.mp4
```

Inspect the generated media with:

```bash
ffprobe \
  -v error \
  -show_entries format=duration,size:stream=codec_name,codec_tag_string,width,height,pix_fmt,r_frame_rate,nb_frames \
  -of default=noprint_wrappers=1 \
  test-card-h265.mp4
```

The file itself is four seconds long. Continuous playback is provided by the demo's `--loop` option, which the Gradle
`runTestCard` task supplies.
