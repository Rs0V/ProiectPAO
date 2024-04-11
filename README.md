# ProiectPAO

A simple 2D game developed in Java using the Java standard library rendering framework, Swing.

## Class Descriptions

The following classes have been created for the project:

- **Vec2**: A vector structure to store information about a 2D point.
- **Transform**: A class that represents properties of an object such as location, rotation, scale, depth.

<hr>

- **Sprite**: A class that contains a BufferedImage and additional properties that describe the image representing a 2D object.

<hr>

- **Shape**: An abstract class used for collision checking.
    - **Rectangle**: Derives from Shape; it represents the collision shape of a rectangle.
    - **Circle**: Derives from Shape; it represents the collision shape of a circle.

<hr>

- **Collider**: The class that is responsible for collision checking calculations; it contains a Shape object.
- **ColliderType**: Enum for the types of colliders that can exist in the game:
    - **Ignore**: The collider will not be able to interact with other colliders.
    - **Overlap**: The collider will generate overlap events, without stopping the movement of the dynamic object interacting with it.
    - **Block**: The collider will stop any moving object colliding with it.

<hr>

- **Actor**: An object that acts in the game (moves, attacks other objects, etc.).
- **Pawn**: An Actor that can take input from the user (used for the player).

<hr>

- **ActorManager**: A service that creates new actors when tasked; manages actors with a sorted (by the y coordinate of the actors) Map.
- **InputMapper**: A service used to assign actions to keyboard keys (generally for handling player input).
