import { AudioPlayer } from '@capacitor-community/audio-player';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    AudioPlayer.echo({ value: inputValue })
}
