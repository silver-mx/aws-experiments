let hitCounter = 0;

exports.handler = async function (event) {
    console.log('request:', JSON.stringify(event, undefined, 2));
    return {
        statusCode: 200,
        headers: { 'Content-Type': "text/plain" },
        body: `Good night, CDK! You have hit ${event.path} ${++hitCounter} times\n`
    };
}