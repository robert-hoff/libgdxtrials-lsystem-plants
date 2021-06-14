#ifdef GL_ES
precision mediump float;
#endif

// varying vec2 v_texCoord0;
uniform vec3 u_color;

void main() {
    // gl_FragColor = vec4(v_texCoord0, 0.0, 1.0);
    gl_FragColor = vec4(u_color, 1.0);
}




