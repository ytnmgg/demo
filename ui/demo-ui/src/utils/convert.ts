export const makeOptionsFromMap = (o: object) => {
    var r: Array<object> = []

    for (const key in o) {
        var k = key
        if (k !== 'default') {
            r.push({ 'value': k, 'label': o[key] });
        }
    }
    return r;
}