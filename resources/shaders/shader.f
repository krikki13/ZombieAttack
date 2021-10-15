#version 330

struct SpotLight {
    vec3 position;
	vec3 spotColor;
    float intensity;
    vec3 coneDirection;
    float cutOffAngle;
};

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    int hasTexture;
    float reflectance;
    int hasNormalMap;
};

struct Fog {
    vec3 color;
    float density;
};

in vec2 texCoord;
in vec3 trNormal;
in vec4 position;

out vec4 fragColor;

uniform sampler2D texSampler;
uniform vec4 color;

uniform mat4 mvMatrix;
uniform sampler2D normalMap;

uniform Material material;
uniform SpotLight spotLight[SPOT_LIGHT_COUNT];
uniform Fog fog;

vec3 getNormal() {
    if (material.hasNormalMap == 1) {
        vec3 normal = texture(normalMap, texCoord).rgb;
        normal = normalize(normal * 2 - 1);
        return normalize(mvMatrix * vec4(normal, 0.0)).xyz;
    } else {
        return normalize(trNormal);
    }
}

vec4 getSpotLightColor(SpotLight light, float opacity) {
    vec3 lightDir = normalize(light.position - position.xyz);
    vec3 normal = getNormal();
    vec3 eyeDir = normalize(-position.xyz);
    vec3 reflectionDir = normalize(reflect(-lightDir, -normal));
    float specWeight = pow(max(dot(reflectionDir, eyeDir), 0.0), material.reflectance);

    float diffuseWeight = max(dot(normal, lightDir), 0.0);


    float distanceWeight = 70/pow(distance(light.position, position.xyz), 2);

    float alfa = dot(lightDir, normalize(light.coneDirection));

    vec3 lightWeight;
    if (alfa > light.cutOffAngle) {
		lightWeight = light.spotColor * light.intensity * distanceWeight * (material.specular * specWeight + material.diffuse * diffuseWeight * opacity);
        lightWeight *= (1.0 - (1.0 - alfa) / (1.0 - light.cutOffAngle));
		return vec4(lightWeight, max(specWeight*(material.specular.r+material.specular.g+material.specular.b)/3, opacity));
    } else {
        return vec4(0, 0, 0, 0);
    }
}

vec4 getFogValue(vec4 color) {
    float dist = length(position);
    float fogFactor = 0.8 / exp(pow(dist * fog.density, 2));
    fogFactor = clamp( fogFactor, 0.0, 1.0 );
    vec3 resultColor = mix(fog.color, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

void main() {
    vec4 fragmentColor = vec4(1.0, 1.0, 1.0, 1.0);
    if (material.hasTexture == 1) {
        fragmentColor = texture(texSampler, texCoord);
    } else {
        fragmentColor = color;
    }
	// če je tekstura 100% prozorna na njej ne bo odseva (in še preskoč se računanje za brezveze)
	if(fragmentColor.a == 0.0){
		fragColor = vec4(0,0,0,0);
		return;
	}

    vec3 lightWeight = material.ambient;
	float specMax = 0;
    for (int i = 0; i < SPOT_LIGHT_COUNT; i++) {
		vec4 temp = getSpotLightColor(spotLight[i], fragmentColor.a);
        lightWeight += temp.rgb;
		specMax = max(temp.a, specMax);
    }

    fragColor = vec4(fragmentColor.rgb * lightWeight, max(fragmentColor.a, specMax));
    fragColor = getFogValue(fragColor);
}