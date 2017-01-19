# Archie.ai Android Client

This Android application is the primary interface to the Archie intelligent agent. Includes
facilities for recording and recognizing user speech, extracting simple actions, and playing back
spoken responses.

At present, only recording and speech playback are done on-device. Speech recognition, natural
language understanding, and speech synthesis are all done in the ArchieService.

The current workflow is as follows:
1. User presses 'microphone' button to begin speech recording. Pressing again stops recording.
2. Recording is uploaded via HTTP to ArchieService endpoint where transcription and intent
   recognition are performed if possible, generating a ClientAction.
3. The device processes the received ClientAction which includes text to display on-screen and text
   to speak.
4. An additional request synthesizes speech in the ArchieService and streams it to the device for
   playback.

Additional activities to test the speech recognition and synthesis directly are provided for
debugging purposes.