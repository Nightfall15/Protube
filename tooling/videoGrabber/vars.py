"""
Each one of the following variables holds the name of the program related to it.
For instance, FFMPEG_BIN in Windows would be ffmpeg.exe, whereas in Linux/macOS would be ffmpeg
If these programs cannot be located through your PATH environment variable, either:
 - modify PATH
 - include the absolute path in the variable content. Example for yt-dlp
Remember you can download the yt-dlp matching your system here:
https://github.com/yt-dlp/yt-dlp?tab=readme-ov-file#release-files

This has been added to the gitignore, so each team member can have an specific configuration
"""


# Here you have your own path
YT_DLP_BIN="C:\Apps\Projects\Repos\yt-dlp.exe"
FFMPEG_BIN=r"C:\Apps\Projects\Repos\ffmpeg-2025-09-25-git-9970dc32bf-full_build\bin"
FFPROBE_BIN=r"C:\Apps\Projects\Repos\ffmpeg-2025-09-25-git-9970dc32bf-full_build\bin"