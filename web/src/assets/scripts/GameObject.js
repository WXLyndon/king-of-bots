const GAME_OBJECTS = [];

export class GameObject {
  constructor() {
    GAME_OBJECTS.push(this);

    this.timeDelta = 0;
    this.has_called_start = false;
  }

  // Only executes once when constructed.
  start() {}

  // Executes every frame except the first one.
  update() {}

  // Executes before destroyed.
  on_destroy() {}

  // Executes when destroyed.
  destroy() {
    this.on_destroy();

    for (let i in GAME_OBJECTS) {
      const gameObject = GAME_OBJECTS[i];
      if (gameObject === this) {
        GAME_OBJECTS.splice(i);
        break;
      }
    }
  }
}

let last_timestamp; // Last excuted time
const step = (timestamp) => {
  for (let obj of GAME_OBJECTS) {
    if (!obj.has_called_start) {
      obj.has_called_start = true;
      obj.start();
    } else {
      obj.timeDelta = timestamp - last_timestamp;
      obj.update();
    }
  }

  last_timestamp = timestamp;
  requestAnimationFrame(step);
};

requestAnimationFrame(step);
