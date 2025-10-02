interface Thumbnail {
  url: string,
  height: number,
  width: number
}

export interface Audio {
  id: string,
  title: string,
  duration: number,
  thumbnails: Thumbnail[],
  author: string,
  artist: string,
  url: string
}

export interface AudioPlayerPlugin {
  play(options: Audio): Promise<void>;
}
