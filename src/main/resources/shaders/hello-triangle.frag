/*
 * Fragment shader.
 */
#version 450

// Output
#define FRAG_COLOR  0

precision highp float;
precision highp int;
layout(std140, column_major) uniform;
layout(std430, column_major) buffer;

// Incoming interpolated (between vertices) color.
layout (location = 0) in vec3 interpolatedColor;


// Outgoing final color.
layout (location = FRAG_COLOR) out vec4 outputColor;

void main() {
    // We simply pad the interpolatedColor
    outputColor = vec4(interpolatedColor, 1.0);
}
