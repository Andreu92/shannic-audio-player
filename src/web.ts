import { WebPlugin } from '@capacitor/core';

import type { Audio, AudioPlayerPlugin } from './definitions';

export class AudioPlayerWeb extends WebPlugin implements AudioPlayerPlugin {
  async play(options: Audio): Promise<void> {
    console.log('not implemented...', options);
  }
}
