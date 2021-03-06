/*
 * Vertex shader.
 */
#version 450
// Vertex attributes
#define POSITION    0
// Uniform
#define TRANSFORM0  1
// Interfaces
#define BLOCK       0

precision highp float;
precision highp int;
layout(std140, column_major) uniform;
layout(std430, column_major) buffer;

layout (location = POSITION) in vec3 position;

// Uniform matrix from Model Space to Clip Space.
layout (binding = TRANSFORM0) uniform Transform
{
    mat4 modelToClipMatrix;
} transform;

// Outgoing color.
layout (location = 0) out vec3 interpolatedColor;

void main() {

    // Normally gl_Position is in Clip Space and we calculate it by multiplying 
    // it with the modelToClipMatrix.
    gl_Position = transform.modelToClipMatrix * vec4(position, 1.0);

    // We assign the color to the outgoing variable.
    interpolatedColor = vec3(clamp(position, 0, 1));
}
