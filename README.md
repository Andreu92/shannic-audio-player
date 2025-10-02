# @shannic/audio-player

Shannic audio player plugin.

## Install

```bash
npm install @shannic/audio-player
npx cap sync
```

## API

<docgen-index>

* [`play(...)`](#play)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### play(...)

```typescript
play(options: Audio) => Promise<void>
```

| Param         | Type                                    |
| ------------- | --------------------------------------- |
| **`options`** | <code><a href="#audio">Audio</a></code> |

--------------------


### Interfaces


#### Audio

| Prop             | Type                     |
| ---------------- | ------------------------ |
| **`id`**         | <code>string</code>      |
| **`title`**      | <code>string</code>      |
| **`duration`**   | <code>number</code>      |
| **`thumbnails`** | <code>Thumbnail[]</code> |
| **`author`**     | <code>string</code>      |
| **`artist`**     | <code>string</code>      |
| **`url`**        | <code>string</code>      |


#### Thumbnail

| Prop         | Type                |
| ------------ | ------------------- |
| **`url`**    | <code>string</code> |
| **`height`** | <code>number</code> |
| **`width`**  | <code>number</code> |

</docgen-api>
